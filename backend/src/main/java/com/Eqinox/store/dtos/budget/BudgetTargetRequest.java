package com.Eqinox.store.dtos.budget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BudgetTargetRequest {

    @NotNull
    private Integer categoryId;

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String frequency; // DAILY/WEEKLY/MONTHLY/YEARLY/CUSTOM

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
