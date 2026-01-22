package com.Eqinox.store.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTransactionRequest {

    private Integer categoryId;
    private BigDecimal amount;
    private LocalDate date;
    private String note;

    public Integer getCategoryId() {
        return categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}
