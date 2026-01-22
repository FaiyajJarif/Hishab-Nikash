package com.Eqinox.store.dtos;

import com.Eqinox.store.entities.FamilyRole;
import com.Eqinox.store.entities.InvitationStatus;

import java.time.OffsetDateTime;

public class InviteFamilyResponse {
    private Long invitationId;
    private Integer familyId;
    private String invitedEmail;
    private FamilyRole role;
    private InvitationStatus status;
    private OffsetDateTime expiresAt;

    public InviteFamilyResponse(Long invitationId, Integer familyId, String invitedEmail,
                                FamilyRole role, InvitationStatus status, OffsetDateTime expiresAt) {
        this.invitationId = invitationId;
        this.familyId = familyId;
        this.invitedEmail = invitedEmail;
        this.role = role;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public Long getInvitationId() { return invitationId; }
    public Integer getFamilyId() { return familyId; }
    public String getInvitedEmail() { return invitedEmail; }
    public FamilyRole getRole() { return role; }
    public InvitationStatus getStatus() { return status; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
}
