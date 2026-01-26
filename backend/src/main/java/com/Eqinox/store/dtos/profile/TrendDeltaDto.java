package com.Eqinox.store.dtos.profile;

public record TrendDeltaDto(
    double incomeChangePct,
    double spendingChangePct,
    double savingsChangePct
) {}

