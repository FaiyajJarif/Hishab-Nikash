package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "user_bank_links",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "bank_account_id"})
    }
)
public class UserBankLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "linked_at", nullable = false)
    private OffsetDateTime linkedAt = OffsetDateTime.now();

    // ========================
    // GETTERS & SETTERS
    // ========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public OffsetDateTime getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(OffsetDateTime linkedAt) {
        this.linkedAt = linkedAt;
    }
}
