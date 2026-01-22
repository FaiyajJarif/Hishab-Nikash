package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;

public class CategoryBreakdownDto {
    private Integer categoryId;
    private String categoryName;
    private BigDecimal amount;

    public CategoryBreakdownDto(Integer categoryId, String categoryName, BigDecimal amount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
    }

    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getAmount() { return amount; }
}
