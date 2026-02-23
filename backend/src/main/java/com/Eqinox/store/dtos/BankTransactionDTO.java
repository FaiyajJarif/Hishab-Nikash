package com.Eqinox.store.dtos;

import com.Eqinox.store.entities.BankTransaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class BankTransactionDTO {

    private Integer id;
    private String provider;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String status;
    private String type;
    private String description;
    private OffsetDateTime createdAt;

    public BankTransactionDTO(BankTransaction txn) {
        this.id = txn.getId();
        this.provider = txn.getBankAccount().getProvider().name();
        this.accountNumber = txn.getBankAccount().getAccountNumber();
        this.amount = txn.getAmount();
        this.balanceAfter = txn.getBalanceAfter();
        this.status = txn.getStatus();
        this.type = txn.getType();
        this.description = txn.getDescription();
        this.createdAt = txn.getCreatedAt();
    }

    // getters only
    public Integer getId() { return id; }
    public String getProvider() { return provider; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
