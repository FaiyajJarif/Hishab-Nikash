package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CategoryAnomalyDto {

    private Integer categoryId;
    private String categoryName;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal average;
    private BigDecimal multiplier;

    public CategoryAnomalyDto(
            Integer categoryId,
            String categoryName,
            LocalDate date,
            BigDecimal amount,
            BigDecimal average,
            BigDecimal multiplier) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.date = date;
        this.amount = amount;
        this.average = average;
        this.multiplier = multiplier;
    }

    // getters
    public Integer getCategoryId() {
        return categoryId;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public LocalDate getDate() {
        return date;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public BigDecimal getAverage() {
        return average;
    }
    public BigDecimal getMultiplier() {
        return multiplier;
    }
}
