package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "bank_accounts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "account_number"})
    }
)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_account_id")
    private Integer bankAccountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankProvider provider;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String pin; // mock PIN authentication

    @Column(name = "mock_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal mockBalance = BigDecimal.ZERO;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // ========================
    // GETTERS & SETTERS
    // ========================

    public Integer getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Integer bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public BankProvider getProvider() {
        return provider;
    }

    public void setProvider(BankProvider provider) {
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public BigDecimal getMockBalance() {
        return mockBalance;
    }

    public void setMockBalance(BigDecimal mockBalance) {
        this.mockBalance = mockBalance;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
