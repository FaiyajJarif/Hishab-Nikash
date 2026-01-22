package com.Eqinox.store.dtos.alerts;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OverspendAlertDto {
    private String type; // "CATEGORY_OVERSPEND" | "MONTH_OVERSPEND"
    private Integer categoryId;
    private String categoryName;
    private BigDecimal planned;
    private BigDecimal actual;
    private BigDecimal overBy;
    private LocalDate date;
    private String message;

    public OverspendAlertDto() {}

    public OverspendAlertDto(String type, Integer categoryId, String categoryName,
                             BigDecimal planned, BigDecimal actual, BigDecimal overBy,
                             LocalDate date, String message) {
        this.type = type;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.planned = planned;
        this.actual = actual;
        this.overBy = overBy;
        this.date = date;
        this.message = message;
    }

    // getters/setters...
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getPlanned() { return planned; }
    public void setPlanned(BigDecimal planned) { this.planned = planned; }

    public BigDecimal getActual() { return actual; }
    public void setActual(BigDecimal actual) { this.actual = actual; }

    public BigDecimal getOverBy() { return overBy; }
    public void setOverBy(BigDecimal overBy) { this.overBy = overBy; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
