package com.Eqinox.store.dtos;

import com.Eqinox.store.entities.FamilyRole;

public class InviteFamilyRequest {
    private String email;
    private FamilyRole role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public FamilyRole getRole() { return role; }
    public void setRole(FamilyRole role) { this.role = role; }
}
