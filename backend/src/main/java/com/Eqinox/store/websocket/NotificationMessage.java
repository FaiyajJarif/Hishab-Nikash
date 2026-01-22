package com.Eqinox.store.websocket;

import java.time.LocalDate;
import java.math.BigDecimal;

public class NotificationMessage {

    private String type; // DAILY_OVERSPEND
    private LocalDate date;
    private BigDecimal amount;
    private String message;

    public NotificationMessage() {}

    public NotificationMessage(
            String type,
            LocalDate date,
            BigDecimal amount,
            String message) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.message = message;
    }

    public NotificationMessage(String message) {
        this.type = "TEST";
        this.date = LocalDate.now();
        this.amount = BigDecimal.ZERO;
        this.message = message;
    }

    public String getType() { return type; }
    public LocalDate getDate() { return date; }
    public BigDecimal getAmount() { return amount; }
    public String getMessage() { return message; }
}
