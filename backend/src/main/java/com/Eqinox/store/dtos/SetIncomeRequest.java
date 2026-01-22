package com.Eqinox.store.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class SetIncomeRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Planned amount cannot be negative")
    private BigDecimal income;
    private Integer month;
    private Integer year;

    public SetIncomeRequest() {}

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
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
