package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class CreateGoalRequest {
    private String name;
    private BigDecimal targetAmount;
    private String note;

    public String getName() { return name; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public String getNote() { return note; }
    public void setName(String name) { this.name = name; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public void setNote(String note) { this.note = note; }
}
