package com.Eqinox.store.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_onboarding_selections")
public class UserOnboardingSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    @Column(nullable = false)
    private String step; // <-- ADD THIS

    @Column(nullable = false)
    private String category;

    private String value;
    private String frequency;

    public UserOnboardingSelection() {
    }

    public UserOnboardingSelection(
            Integer userId,
            String step,
            String category,
            String value,
            String frequency) {
        this.userId = userId;
        this.step = step;
        this.category = category;
        this.value = value;
        this.frequency = frequency;
    }

    // getters & setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getStep() {
        return step;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
}
