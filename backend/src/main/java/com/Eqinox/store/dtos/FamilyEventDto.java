package com.Eqinox.store.dtos;

import java.time.OffsetDateTime;

public class FamilyEventDto {

    private Integer familyId;
    private String type;      // e.g. "BUDGET_UPDATED", "TRANSACTION_ADDED"
    private String message;   // human readable
    private OffsetDateTime at;

    public FamilyEventDto() {}

    public FamilyEventDto(Integer familyId, String type, String message, OffsetDateTime at) {
        this.familyId = familyId;
        this.type = type;
        this.message = message;
        this.at = at;
    }

    public Integer getFamilyId() { return familyId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public OffsetDateTime getAt() { return at; }

    public void setFamilyId(Integer familyId) { this.familyId = familyId; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setAt(OffsetDateTime at) { this.at = at; }
}
