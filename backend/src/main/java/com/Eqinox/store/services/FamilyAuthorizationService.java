package com.Eqinox.store.services;

import com.Eqinox.store.entities.FamilyMember;
import com.Eqinox.store.entities.FamilyRole;
import com.Eqinox.store.repositories.FamilyMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyAuthorizationService {

    private final FamilyMemberRepository memberRepo;

    public FamilyAuthorizationService(FamilyMemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    // ✅ NEW — used by FamilyBudgetController
    public void authorizeFamilyView(Integer familyId, Integer userId) {
        memberRepo.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new RuntimeException("Not a member of this family"));
    }

    // ✅ NEW — used by FamilyBudgetController & FamilyBudgetService
    public void authorizeFamilyEdit(Integer familyId, Integer userId) {
        FamilyMember m = memberRepo.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new RuntimeException("Not a member of this family"));

        if (m.getRole() == FamilyRole.VIEWER) {
            throw new RuntimeException("VIEWER cannot modify shared family budget");
        }
    }

    // 🔁 KEEP your existing method (used elsewhere)
    public void authorizeBudgetEdit(List<FamilyMember> memberships) {
        if (memberships == null || memberships.isEmpty()) return;

        boolean allowed = memberships.stream()
                .anyMatch(m ->
                        m.getRole() == FamilyRole.ADMIN ||
                        m.getRole() == FamilyRole.EDITOR
                );

        if (!allowed) {
            throw new RuntimeException("VIEWER cannot modify shared family budget");
        }
    }

    public void authorizeFamilyAdmin(Integer familyId, Integer userId) {
        FamilyMember m = memberRepo.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new RuntimeException("Not a member of this family"));
    
        if (m.getRole() != FamilyRole.ADMIN) {
            throw new RuntimeException("ADMIN only action");
        }
    }    
}
