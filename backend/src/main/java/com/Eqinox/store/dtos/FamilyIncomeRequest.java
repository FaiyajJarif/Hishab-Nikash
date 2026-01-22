package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class FamilyIncomeRequest {
    private Integer month;
    private Integer year;
    private BigDecimal income;

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
}
