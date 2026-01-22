package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_contributions")
public class GoalContribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer goalId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters...
    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getGoalId() { return goalId; }
    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public BigDecimal getAmount() { return amount; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setGoalId(Integer goalId) { this.goalId = goalId; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
