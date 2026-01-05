package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class PlanBudgetRequest {

    private Integer categoryId;
    private BigDecimal amount;

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
}

