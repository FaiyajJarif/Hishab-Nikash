package com.Eqinox.store.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CategorySpendingAnomalyDto {

    private Integer categoryId;
    private String categoryName;
    private LocalDate date;
    private BigDecimal todaySpend;
    private BigDecimal averageSpend;
    private BigDecimal percentageIncrease;

    // getters & setters
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

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
}
