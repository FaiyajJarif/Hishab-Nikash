package com.Eqinox.store.dtos;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class PlanBudgetRequest {

    private Integer categoryId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Planned amount cannot be negative")
    private BigDecimal amount;
    private Integer month;
    private Integer year;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}

