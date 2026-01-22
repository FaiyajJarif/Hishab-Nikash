package com.Eqinox.store.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;

@Service
public class FamilyService {

    private final FamilyGroupRepository familyRepo;
    private final FamilyMemberRepository memberRepo;
    private final FamilySyncService familySyncService;
    private final FamilyActivityService familyActivityService;

    public FamilyService(
            FamilyGroupRepository familyRepo,
            FamilyMemberRepository memberRepo,
            FamilySyncService familySyncService,
            FamilyActivityService familyActivityService) {
        this.familyRepo = familyRepo;
        this.memberRepo = memberRepo;
        this.familySyncService = familySyncService;
        this.familyActivityService = familyActivityService;
    }

    // 1️⃣ Create family
    @Transactional
    public FamilyGroup createFamily(String name, Integer ownerId) {

        FamilyGroup family = new FamilyGroup();
        family.setName(name);
        family.setOwnerId(ownerId);
        family.setCreatedAt(LocalDateTime.now());

        FamilyGroup saved = familyRepo.save(family);

        FamilyMember owner = new FamilyMember();
        owner.setFamilyId(saved.getId());
        owner.setUserId(ownerId);
        owner.setRole(FamilyRole.ADMIN);
        owner.setJoinedAt(LocalDateTime.now());

        memberRepo.save(owner);
        return saved;
    }

    // 2️⃣ Invite member (ADMIN only)
    public void addMember(Integer familyId, Integer userId, FamilyRole role, Integer inviterId) {

        FamilyMember inviter = memberRepo
                .findByFamilyIdAndUserId(familyId, inviterId)
                .orElseThrow(() -> new RuntimeException("Not a family member"));

        if (inviter.getRole() != FamilyRole.ADMIN) {
            throw new RuntimeException("Only ADMIN can invite members");
        }

        if (memberRepo.findByFamilyIdAndUserId(familyId, userId).isPresent()) {
            throw new RuntimeException("User already in family");
        }

        FamilyMember member = new FamilyMember();
        member.setFamilyId(familyId);
        member.setUserId(userId);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());

        memberRepo.save(member);
    }

    // 3️⃣ Get my families
    public List<FamilyMember> getMyFamilies(Integer userId) {
        return memberRepo.findByUserId(userId);
    }

    // 4️⃣ Get family members
    public List<FamilyMember> getFamilyMembers(Integer familyId, Integer requesterId) {

        authorizeMember(familyId, requesterId);
        return memberRepo.findByFamilyId(familyId);
    }

    // 5️⃣ Remove member (ADMIN)
    public void removeMember(Integer familyId, Integer targetUserId, Integer adminId) {

        FamilyMember admin = authorizeAdmin(familyId, adminId);

        FamilyMember target = memberRepo
                .findByFamilyIdAndUserId(familyId, targetUserId)
                .orElseThrow(() -> new RuntimeException("User not in family"));

        memberRepo.delete(target);
    }

    // 6️⃣ Leave family
    public void leaveFamily(Integer familyId, Integer userId) {

        FamilyMember member = memberRepo
                .findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new RuntimeException("Not a family member"));

        if (member.getRole() == FamilyRole.ADMIN) {
            throw new RuntimeException("ADMIN cannot leave family");
        }

        memberRepo.delete(member);
    }

    // 🔐 Helpers
    private void authorizeMember(Integer familyId, Integer userId) {
        memberRepo.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));
    }

    private FamilyMember authorizeAdmin(Integer familyId, Integer userId) {
        FamilyMember member = memberRepo
                .findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        if (member.getRole() != FamilyRole.ADMIN) {
            throw new RuntimeException("Admin only action");
        }
        return member;
    }

    public void changeRole(
            Integer familyId,
            Integer targetUserId,
            FamilyRole newRole,
            Integer adminId) {

        authorizeAdmin(familyId, adminId);

        FamilyMember target = memberRepo
                .findByFamilyIdAndUserId(familyId, targetUserId)
                .orElseThrow(() -> new RuntimeException("User not in family"));

        // Prevent admin changing own role
        if (targetUserId.equals(adminId)) {
            throw new RuntimeException("Admin cannot change own role");
        }

        // Prevent demoting ADMIN
        if (target.getRole() == FamilyRole.ADMIN) {
            throw new RuntimeException("Cannot change ADMIN role");
        }

        target.setRole(newRole);
        memberRepo.save(target);
    }

}
