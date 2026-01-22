package com.Eqinox.store.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SpendingAnomalyDto {

    private LocalDate date;
    private BigDecimal todaySpend;
    private BigDecimal averageSpend;
    private BigDecimal percentageIncrease;
    private String message;

    // getters & setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getTodaySpend() { return todaySpend; }
    public void setTodaySpend(BigDecimal todaySpend) { this.todaySpend = todaySpend; }

    public BigDecimal getAverageSpend() { return averageSpend; }
    public void setAverageSpend(BigDecimal averageSpend) { this.averageSpend = averageSpend; }

    public BigDecimal getPercentageIncrease() { return percentageIncrease; }
    public void setPercentageIncrease(BigDecimal percentageIncrease) {
        this.percentageIncrease = percentageIncrease;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
