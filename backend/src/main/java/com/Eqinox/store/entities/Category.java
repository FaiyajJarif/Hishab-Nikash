package com.Eqinox.store.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 100)
    private String name;

    /**
     * EXPENSE, GOAL, DEBT, SUBSCRIPTION
     */
    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "is_goal")
    private Boolean isGoal = false;

    @Column(name = "goal_amount")
    private BigDecimal goalAmount;

    @Column(name = "goal_frequency")
    private String goalFrequency; // MONTHLY, WEEKLY

    @Column(nullable = false)
    private Boolean rolloverEnabled = true;

    public Category() {
    }

    public Category(Integer userId, String name, String type) {
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Boolean getIsGoal() {
        return isGoal;
    }
    public void setIsGoal(Boolean isGoal) {
        this.isGoal = isGoal;
    }
    public BigDecimal getGoalAmount() {
        return goalAmount;
    }
    public void setGoalAmount(BigDecimal goalAmount) {
        this.goalAmount = goalAmount;
    }
    public String getGoalFrequency() {
        return goalFrequency;
    }
    public void setGoalFrequency(String goalFrequency) {
        this.goalFrequency = goalFrequency;
    }
    public Boolean getRolloverEnabled() {
        return rolloverEnabled;
    }
    public void setRolloverEnabled(Boolean rolloverEnabled) {
        this.rolloverEnabled = rolloverEnabled;
    }
}
