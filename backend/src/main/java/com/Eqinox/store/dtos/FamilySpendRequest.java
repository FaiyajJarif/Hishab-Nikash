package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class FamilySpendRequest {
    private Integer month;
    private Integer year;
    private Integer categoryId;
    private BigDecimal amount;
    private String note;

    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public Integer getCategoryId() { return categoryId; }
    public BigDecimal getAmount() { return amount; }
    public String getNote() {
        return note;
    }
}
