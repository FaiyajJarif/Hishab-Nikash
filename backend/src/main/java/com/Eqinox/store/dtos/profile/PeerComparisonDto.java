package com.Eqinox.store.dtos.profile;

public record PeerComparisonDto(
    String ageRange,
    double yourMonthlySpending,
    double peerAverageSpending,
    double differencePercentage
) {}
