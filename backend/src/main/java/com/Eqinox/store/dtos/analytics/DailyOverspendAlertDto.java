package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyOverspendAlertDto {

    private LocalDate date;
    private BigDecimal todayExpense;
    private BigDecimal averageDailyExpense;
    private boolean overspent;
    private BigDecimal difference;

    public DailyOverspendAlertDto(
            LocalDate date,
            BigDecimal todayExpense,
            BigDecimal averageDailyExpense,
            boolean overspent,
            BigDecimal difference
    ) {
        this.date = date;
        this.todayExpense = todayExpense;
        this.averageDailyExpense = averageDailyExpense;
        this.overspent = overspent;
        this.difference = difference;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getTodayExpense() {
        return todayExpense;
    }

    public BigDecimal getAverageDailyExpense() {
        return averageDailyExpense;
    }

    public boolean isOverspent() {
        return overspent;
    }

    public BigDecimal getDifference() {
        return difference;
    }
}
