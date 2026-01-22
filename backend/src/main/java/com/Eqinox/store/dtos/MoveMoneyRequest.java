package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public class MoveMoneyRequest {
    public Integer fromCategoryId;
    public Integer toCategoryId;
    public BigDecimal amount;
    public Integer month;
    public Integer year;
}

