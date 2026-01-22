package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_activity")
public class FamilyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer familyId;

    @Column(nullable = false)
    private Integer actorUserId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters & setters
    public Long getId() { return id; }
    public Integer getFamilyId() { return familyId; }
    public void setFamilyId(Integer familyId) { this.familyId = familyId; }
    public Integer getActorUserId() { return actorUserId; }
    public void setActorUserId(Integer actorUserId) { this.actorUserId = actorUserId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
