package com.Eqinox.store.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BillNotificationDto {

    private Integer billId;
    private String billName;
    private BigDecimal amount;
    private LocalDate date;
    private String message;

    public BillNotificationDto(
            Integer billId,
            String billName,
            BigDecimal amount,
            LocalDate date,
            String message) {

        this.billId = billId;
        this.billName = billName;
        this.amount = amount;
        this.date = date;
        this.message = message;
    }

    public Integer getBillId() { return billId; }
    public String getBillName() { return billName; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getMessage() { return message; }
}
