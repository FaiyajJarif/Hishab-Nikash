package com.Eqinox.store.dtos;

import java.time.OffsetDateTime;

public class FamilyEventDto {

    private Integer familyId;
    private String type;
    private String message;
    private String actorName;     // ✅ ADD THIS
    private OffsetDateTime at;

    public FamilyEventDto() {}

    public FamilyEventDto(
            Integer familyId,
            String type,
            String message,
            String actorName,
            OffsetDateTime at
    ) {
        this.familyId = familyId;
        this.type = type;
        this.message = message;
        this.actorName = actorName;
        this.at = at;
    }

    public Integer getFamilyId() { return familyId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getActorName() { return actorName; }   // ✅
    public OffsetDateTime getAt() { return at; }

    public void setFamilyId(Integer familyId) { this.familyId = familyId; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setActorName(String actorName) { this.actorName = actorName; } // ✅
    public void setAt(OffsetDateTime at) { this.at = at; }
}
