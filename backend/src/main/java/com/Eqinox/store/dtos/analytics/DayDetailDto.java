package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DayDetailDto {
    private LocalDate date;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;
    private long transactionCount;

    private List<CategoryBreakdownDto> categories;
    private List<TransactionListItemDto> transactions;

    public DayDetailDto(LocalDate date, BigDecimal income, BigDecimal expense, BigDecimal net,
                       long transactionCount,
                       List<CategoryBreakdownDto> categories,
                       List<TransactionListItemDto> transactions) {
        this.date = date;
        this.income = income;
        this.expense = expense;
        this.net = net;
        this.transactionCount = transactionCount;
        this.categories = categories;
        this.transactions = transactions;
    }

    public LocalDate getDate() { return date; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
    public BigDecimal getNet() { return net; }
    public long getTransactionCount() { return transactionCount; }
    public List<CategoryBreakdownDto> getCategories() { return categories; }
    public List<TransactionListItemDto> getTransactions() { return transactions; }
}
