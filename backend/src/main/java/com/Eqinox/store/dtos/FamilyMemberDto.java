package com.Eqinox.store.dtos;

import java.time.LocalDateTime;
import com.Eqinox.store.entities.FamilyRole;

public class FamilyMemberDto {

    private Integer userId;
    private String name;
    private String email;
    private FamilyRole role;
    private LocalDateTime joinedAt;

    // 🔥 REQUIRED constructor (matches JPQL)
    public FamilyMemberDto(
        Integer userId,
        String name,
        String email,
        FamilyRole role,
        LocalDateTime joinedAt
    ) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    // getters (required for JSON)
    public Integer getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public FamilyRole getRole() { return role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}
