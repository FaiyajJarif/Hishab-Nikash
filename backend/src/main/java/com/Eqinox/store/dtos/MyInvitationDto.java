package com.Eqinox.store.dtos;

import com.Eqinox.store.entities.FamilyRole;
import com.Eqinox.store.entities.InvitationStatus;

import java.time.OffsetDateTime;

public class MyInvitationDto {
    private Long id;
    private Integer familyId;
    private String invitedEmail;
    private FamilyRole role;
    private InvitationStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
    private String token; // useful for testing in Postman

    public MyInvitationDto(Long id, Integer familyId, String invitedEmail, FamilyRole role,
                           InvitationStatus status, OffsetDateTime createdAt, OffsetDateTime expiresAt, String token) {
        this.id = id;
        this.familyId = familyId;
        this.invitedEmail = invitedEmail;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.token = token;
    }

    public Long getId() { return id; }
    public Integer getFamilyId() { return familyId; }
    public String getInvitedEmail() { return invitedEmail; }
    public FamilyRole getRole() { return role; }
    public InvitationStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public String getToken() { return token; }
}
