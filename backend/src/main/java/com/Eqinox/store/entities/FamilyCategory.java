package com.Eqinox.store.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "family_categories")
public class FamilyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer familyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // "Subscription", "Lifestyle", "Goal", etc.

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFamilyId() { return familyId; }
    public void setFamilyId(Integer familyId) { this.familyId = familyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
