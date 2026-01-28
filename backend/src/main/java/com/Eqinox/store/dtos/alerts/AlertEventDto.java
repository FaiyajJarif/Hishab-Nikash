package com.Eqinox.store.dtos.alerts;

import java.time.OffsetDateTime;

public record AlertEventDto(
    String message,
    String severity,
    OffsetDateTime at
) {}

