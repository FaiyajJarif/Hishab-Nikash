package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CalendarDayDto {
    private LocalDate date;
    private BigDecimal expense;

    public CalendarDayDto(LocalDate date, BigDecimal expense) {
        this.date = date;
        this.expense = expense;
    }

    public LocalDate getDate() { return date; }
    public BigDecimal getExpense() { return expense; }
}
