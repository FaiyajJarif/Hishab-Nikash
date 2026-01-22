package com.Eqinox.store.services;

import com.Eqinox.store.dtos.InviteFamilyResponse;
import com.Eqinox.store.dtos.MyInvitationDto;
import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FamilyInvitationService {

    private final FamilyInvitationRepository invitationRepo;
    private final FamilyGroupRepository familyRepo;
    private final FamilyMemberRepository memberRepo;
    private final UserRepository userRepo;

    private final FamilyAuthorizationService familyAuth;
    private final FamilyInviteEmailService emailService;
    private final FamilySyncService familySyncService;

    public FamilyInvitationService(
            FamilyInvitationRepository invitationRepo,
            FamilyGroupRepository familyRepo,
            FamilyMemberRepository memberRepo,
            UserRepository userRepo,
            FamilyAuthorizationService familyAuth,
            FamilyInviteEmailService emailService,
            FamilySyncService familySyncService
    ) {
        this.invitationRepo = invitationRepo;
        this.familyRepo = familyRepo;
        this.memberRepo = memberRepo;
        this.userRepo = userRepo;
        this.familyAuth = familyAuth;
        this.emailService = emailService;
        this.familySyncService = familySyncService;
    }

    @Transactional
    public InviteFamilyResponse invite(Integer familyId, Integer inviterUserId, String invitedEmail, FamilyRole role) {
        if (invitedEmail == null || invitedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (role == null) role = FamilyRole.VIEWER;

        // only ADMIN can invite (or allow EDITOR if you want)
        familyAuth.authorizeFamilyAdmin(familyId, inviterUserId);

        FamilyGroup family = familyRepo.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));

        // if invited user exists and already member -> block
        userRepo.findByEmail(invitedEmail).ifPresent(u -> {
            boolean exists = memberRepo.findByFamilyIdAndUserId(familyId, u.getUserId()).isPresent();
            if (exists) throw new RuntimeException("User already in family");
        });

        // create invitation
        FamilyInvitation inv = new FamilyInvitation();
        inv.setFamilyId(familyId);
        inv.setInvitedEmail(invitedEmail.trim().toLowerCase());
        inv.setRole(role);
        inv.setInvitedByUserId(inviterUserId);
        inv.setToken(UUID.randomUUID().toString());
        inv.setStatus(InvitationStatus.PENDING);
        inv.setCreatedAt(OffsetDateTime.now());
        inv.setExpiresAt(OffsetDateTime.now().plusDays(7));

        // if user exists, store invitedUserId (optional)
        userRepo.findByEmail(inv.getInvitedEmail()).ifPresent(u -> inv.setInvitedUserId(u.getUserId()));

        FamilyInvitation saved = invitationRepo.save(inv);

        // send email (inviter name optional)
        String inviterName = userRepo.findById(inviterUserId).map(User::getName).orElse("Someone");
        emailService.sendInviteEmail(inv.getInvitedEmail(), family.getName(), inviterName, saved.getToken());

        // WS: broadcast to family topic (admin sees activity)
        familySyncService.broadcastFamilyEvent(
                familyId,
                inviterUserId,
                "FAMILY_INVITE_SENT",
                "📩 Invitation sent to " + inv.getInvitedEmail()
        );

        return new InviteFamilyResponse(saved.getId(), familyId, inv.getInvitedEmail(), role, saved.getStatus(), saved.getExpiresAt());
    }

    public List<MyInvitationDto> getMyPendingInvites(String myEmail) {
        return invitationRepo.findByInvitedEmailAndStatusOrderByCreatedAtDesc(
                myEmail.trim().toLowerCase(),
                InvitationStatus.PENDING
        ).stream().map(i ->
                new MyInvitationDto(
                        i.getId(),
                        i.getFamilyId(),
                        i.getInvitedEmail(),
                        i.getRole(),
                        i.getStatus(),
                        i.getCreatedAt(),
                        i.getExpiresAt(),
                        i.getToken()
                )
        ).toList();
    }

    @Transactional
    public void accept(String token, Integer actorUserId, String actorEmail) {
        FamilyInvitation inv = invitationRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        ensureCanRespond(inv, actorEmail);

        // expire check
        if (inv.getExpiresAt().isBefore(OffsetDateTime.now())) {
            inv.setStatus(InvitationStatus.EXPIRED);
            inv.setRespondedAt(OffsetDateTime.now());
            invitationRepo.save(inv);
            throw new RuntimeException("Invitation expired");
        }

        // create membership if not exists
        if (memberRepo.findByFamilyIdAndUserId(inv.getFamilyId(), actorUserId).isEmpty()) {
            FamilyMember m = new FamilyMember();
            m.setFamilyId(inv.getFamilyId());
            m.setUserId(actorUserId);
            m.setRole(inv.getRole());
            m.setJoinedAt(java.time.LocalDateTime.now());
            memberRepo.save(m);
        }

        inv.setStatus(InvitationStatus.ACCEPTED);
        inv.setRespondedAt(OffsetDateTime.now());
        invitationRepo.save(inv);

        familySyncService.broadcastFamilyEvent(
                inv.getFamilyId(),
                actorUserId,
                "FAMILY_INVITE_ACCEPTED",
                "✅ " + actorEmail + " joined the family"
        );
    }

    @Transactional
    public void reject(String token, Integer actorUserId, String actorEmail) {
        FamilyInvitation inv = invitationRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        ensureCanRespond(inv, actorEmail);

        inv.setStatus(InvitationStatus.REJECTED);
        inv.setRespondedAt(OffsetDateTime.now());
        invitationRepo.save(inv);

        familySyncService.broadcastFamilyEvent(
                inv.getFamilyId(),
                actorUserId,
                "FAMILY_INVITE_REJECTED",
                "❌ " + actorEmail + " rejected the invitation"
        );
    }

    private void ensureCanRespond(FamilyInvitation inv, String actorEmail) {
        if (inv.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation is not pending");
        }
        String invited = inv.getInvitedEmail().trim().toLowerCase();
        String actor = actorEmail.trim().toLowerCase();
        if (!invited.equals(actor)) {
            throw new RuntimeException("This invitation is not for your email");
        }
    }
}

