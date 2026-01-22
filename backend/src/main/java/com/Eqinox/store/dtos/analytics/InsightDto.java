package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InsightDto {
    private String type; // "DAILY" or "CATEGORY"
    private LocalDate date;
    private Integer categoryId;
    private BigDecimal today;
    private BigDecimal avg;
    private BigDecimal diff;
    private String message;

    public InsightDto() {}

    public InsightDto(String type, LocalDate date, Integer categoryId,
                      BigDecimal today, BigDecimal avg, BigDecimal diff, String message) {
        this.type = type;
        this.date = date;
        this.categoryId = categoryId;
        this.today = today;
        this.avg = avg;
        this.diff = diff;
        this.message = message;
    }

    // getters/setters...
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public BigDecimal getToday() { return today; }
    public void setToday(BigDecimal today) { this.today = today; }

    public BigDecimal getAvg() { return avg; }
    public void setAvg(BigDecimal avg) { this.avg = avg; }

    public BigDecimal getDiff() { return diff; }
    public void setDiff(BigDecimal diff) { this.diff = diff; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
