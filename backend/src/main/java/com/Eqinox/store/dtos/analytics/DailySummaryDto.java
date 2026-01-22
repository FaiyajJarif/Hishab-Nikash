package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailySummaryDto {
    private LocalDate date;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;
    private long transactionCount;

    public DailySummaryDto(LocalDate date, BigDecimal income, BigDecimal expense, BigDecimal net, long transactionCount) {
        this.date = date;
        this.income = income;
        this.expense = expense;
        this.net = net;
        this.transactionCount = transactionCount;
    }

    public LocalDate getDate() { return date; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
    public BigDecimal getNet() { return net; }
    public long getTransactionCount() { return transactionCount; }
}
