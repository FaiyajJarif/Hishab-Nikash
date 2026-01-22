package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "targets")
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer targetId;

    private Integer userId;
    private Integer categoryId; // nullable for global/monthly target

    private String type;
    // MONTHLY, DAILY, WEEKLY, YEARLY, CUSTOM

    private BigDecimal amount;

    // optional (for CUSTOM)
    private Integer startMonth;
    private Integer startYear;
    private Integer endMonth;
    private Integer endYear;

    public Integer getTargetId() { return targetId; }
    public Integer getUserId() { return userId; }
    public Integer getCategoryId() { return categoryId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
