package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class DashboardSummaryDto {

    private BigDecimal income;
    private BigDecimal assigned;
    private BigDecimal remaining;

    public DashboardSummaryDto() {}

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getAssigned() {
        return assigned;
    }

    public void setAssigned(BigDecimal assigned) {
        this.assigned = assigned;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public void setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
    }
}
