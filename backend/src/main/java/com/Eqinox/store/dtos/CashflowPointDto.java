package com.Eqinox.store.dtos;

import java.math.BigDecimal;

public record CashflowPointDto(
    String month,
    BigDecimal income,
    BigDecimal expense
) {}
