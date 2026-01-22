package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "family_invitations")
public class FamilyInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="family_id", nullable = false)
    private Integer familyId;

    @Column(name="invited_user_id")
    private Integer invitedUserId; // optional (if you invited by email only)

    @Column(name="invited_email", nullable = false)
    private String invitedEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole role;

    @Column(name="invited_by_user_id", nullable = false)
    private Integer invitedByUserId;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name="expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name="responded_at")
    private OffsetDateTime respondedAt;

    // getters/setters
    public Long getId() { return id; }

    public Integer getFamilyId() { return familyId; }
    public void setFamilyId(Integer familyId) { this.familyId = familyId; }

    public Integer getInvitedUserId() { return invitedUserId; }
    public void setInvitedUserId(Integer invitedUserId) { this.invitedUserId = invitedUserId; }

    public String getInvitedEmail() { return invitedEmail; }
    public void setInvitedEmail(String invitedEmail) { this.invitedEmail = invitedEmail; }

    public FamilyRole getRole() { return role; }
    public void setRole(FamilyRole role) { this.role = role; }

    public Integer getInvitedByUserId() { return invitedByUserId; }
    public void setInvitedByUserId(Integer invitedByUserId) { this.invitedByUserId = invitedByUserId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }

    public OffsetDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(OffsetDateTime respondedAt) { this.respondedAt = respondedAt; }
}
