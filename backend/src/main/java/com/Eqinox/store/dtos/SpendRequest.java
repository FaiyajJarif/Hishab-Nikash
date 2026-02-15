package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class SpendRequest {

    private Integer categoryId;
    private BigDecimal amount;
    private Integer month;
    private Integer year;
    private String note;

    public SpendRequest() {
    }

    public SpendRequest(Integer categoryId, BigDecimal amount, Integer month, Integer year, String note) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.note = note;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
