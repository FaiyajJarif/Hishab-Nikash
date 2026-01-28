package com.Eqinox.store.dtos.analytics;

import java.time.LocalDate;

public class DailyCashflowDto {
    private LocalDate date;
    private double expense;
    private double income;

    public DailyCashflowDto(LocalDate date, double expense, double income) {
        this.date = date;
        this.expense = expense;
        this.income = income;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getExpense() {
        return expense;
    }

    public double getIncome() {
        return income;
    }
}