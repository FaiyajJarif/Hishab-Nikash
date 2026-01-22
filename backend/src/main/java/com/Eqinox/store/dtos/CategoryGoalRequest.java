package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class CategoryGoalRequest {
    private Integer categoryId;
    private BigDecimal amount;
    private String frequency; // DAILY, WEEKLY, MONTHLY

    public Integer getCategoryId() { return categoryId; }
    public BigDecimal getAmount() { return amount; }
    public String getFrequency() { return frequency; }
}
