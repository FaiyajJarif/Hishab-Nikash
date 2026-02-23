package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "wallet_ledger_entries")
public class WalletLedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer entryId;

    @Column(name = "wallet_id", nullable = false)
    private Integer walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerType type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    private String description;

    // ✅ FIXED TYPE
    @Column(name = "reference_id", nullable = false, length = 100)
    private String referenceId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status = TransferStatus.SUCCESS;

    // Getters & Setters

    public Integer getEntryId() { return entryId; }

    public Integer getWalletId() { return walletId; }
    public void setWalletId(Integer walletId) { this.walletId = walletId; }

    public LedgerType getType() { return type; }
    public void setType(LedgerType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }
}
