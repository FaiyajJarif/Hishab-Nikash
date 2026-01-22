package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class FamilyPlanRequest {
    private Integer month;
    private Integer year;
    private Integer categoryId;
    private BigDecimal amount;

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
