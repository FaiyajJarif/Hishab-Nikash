package com.Eqinox.store.dtos;

import com.Eqinox.store.entities.BankAccount;

import java.math.BigDecimal;

public class BankAccountDTO {

    private Integer id;
    private String provider;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;

    public BankAccountDTO() {
    }

    public BankAccountDTO(BankAccount account) {
        this.id = account.getBankAccountId();
        this.provider = account.getProvider().name();
        this.accountNumber = account.getAccountNumber();
        this.ownerName = account.getOwnerName();
        this.balance = account.getMockBalance();
    }

    // ========================
    // GETTERS & SETTERS
    // ========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
