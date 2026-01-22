package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RollingAverageDto {

    private LocalDate date;
    private BigDecimal average;

    public RollingAverageDto(LocalDate date, BigDecimal average) {
        this.date = date;
        this.average = average;
    }

    // getters
    public LocalDate getDate() {
        return date;
    }
    public BigDecimal getAverage() {
        return average;
    }
}
