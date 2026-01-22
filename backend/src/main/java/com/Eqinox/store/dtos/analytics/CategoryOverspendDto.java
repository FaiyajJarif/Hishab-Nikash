package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;

public class CategoryOverspendDto {

    private Integer categoryId;
    private String categoryName;
    private BigDecimal planned;
    private BigDecimal actual;
    private BigDecimal overspentAmount;
    private boolean overspent;

    public CategoryOverspendDto(
            Integer categoryId,
            String categoryName,
            BigDecimal planned,
            BigDecimal actual,
            BigDecimal overspentAmount,
            boolean overspent
    ) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.planned = planned;
        this.actual = actual;
        this.overspentAmount = overspentAmount;
        this.overspent = overspent;
    }

    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getPlanned() { return planned; }
    public BigDecimal getActual() { return actual; }
    public BigDecimal getOverspentAmount() { return overspentAmount; }
    public boolean isOverspent() { return overspent; }
}
