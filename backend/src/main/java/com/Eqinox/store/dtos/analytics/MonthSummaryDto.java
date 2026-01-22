package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;

public class MonthSummaryDto {
    private Integer month;
    private Integer year;

    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;

    private BigDecimal assigned;     // planned total (from budget_items)
    private BigDecimal remaining;    // income - assigned
    private BigDecimal unassigned;   // income - assigned (same as remaining here)

    public MonthSummaryDto(Integer month, Integer year,
                           BigDecimal income, BigDecimal expense, BigDecimal net,
                           BigDecimal assigned, BigDecimal remaining, BigDecimal unassigned) {
        this.month = month;
        this.year = year;
        this.income = income;
        this.expense = expense;
        this.net = net;
        this.assigned = assigned;
        this.remaining = remaining;
        this.unassigned = unassigned;
    }

    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
    public BigDecimal getNet() { return net; }
    public BigDecimal getAssigned() { return assigned; }
    public BigDecimal getRemaining() { return remaining; }
    public BigDecimal getUnassigned() { return unassigned; }
}
