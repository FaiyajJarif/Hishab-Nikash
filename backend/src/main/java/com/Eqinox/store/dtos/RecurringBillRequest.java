package com.Eqinox.store.dtos;

import com.Eqinox.store.entities.BillFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecurringBillRequest {

    private String name;
    private BigDecimal amount;
    private BillFrequency frequency; // ✅ enum (NOT String)
    private LocalDate nextDueDate;

    private Integer categoryId; // ✅ REQUIRED because entity has categoryId NOT NULL

    public RecurringBillRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BillFrequency getFrequency() { return frequency; }
    public void setFrequency(BillFrequency frequency) { this.frequency = frequency; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
}
