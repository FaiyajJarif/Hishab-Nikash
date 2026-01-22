package com.Eqinox.store.dtos.recurring;

import com.Eqinox.store.entities.BillFrequency;
import com.Eqinox.store.entities.RecurringBill;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecurringBillDto {

    private Integer id;
    private Integer categoryId;
    private String name;
    private BigDecimal amount;
    private BillFrequency frequency;
    private LocalDate nextDueDate;
    private boolean active;

    public static RecurringBillDto from(RecurringBill b) {
        RecurringBillDto dto = new RecurringBillDto();
        dto.id = b.getId();
        dto.categoryId = b.getCategoryId();
        dto.name = b.getName();
        dto.amount = b.getAmount();
        dto.frequency = b.getFrequency();
        dto.nextDueDate = b.getNextDueDate();
        dto.active = b.isActive();
        return dto;
    }

    public Integer getId() { return id; }
    public Integer getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public BigDecimal getAmount() { return amount; }
    public BillFrequency getFrequency() { return frequency; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public boolean isActive() { return active; }

    public void setId(Integer id) { this.id = id; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setFrequency(BillFrequency frequency) { this.frequency = frequency; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }
    public void setActive(boolean active) { this.active = active; }
}
