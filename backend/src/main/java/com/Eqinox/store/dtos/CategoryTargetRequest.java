package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class CategoryTargetRequest {

    private Integer categoryId;
    private Integer month;
    private Integer year;

    private BigDecimal amount;           // monthly amount
    private BigDecimal totalTargetAmount; // optional
    private String frequency;             // MONTHLY or TOTAL

    public Integer getCategoryId() { return categoryId; }
    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }

    public BigDecimal getAmount() { return amount; }
    public BigDecimal getTotalTargetAmount() { return totalTargetAmount; }
    public String getFrequency() { return frequency; }

    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTotalTargetAmount(BigDecimal totalTargetAmount) {
        this.totalTargetAmount = totalTargetAmount;
    }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}
