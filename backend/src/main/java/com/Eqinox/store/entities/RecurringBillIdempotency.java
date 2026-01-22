package com.Eqinox.store.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "recurring_bill_idempotency",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "idem_key", "operation"})
)
public class RecurringBillIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "idem_key", nullable = false, length = 120)
    private String idemKey;

    @Column(name = "operation", nullable = false, length = 20)
    private String operation; // CREATE / TOGGLE / DELETE

    @Column(name = "bill_id", nullable = false)
    private Integer billId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public static RecurringBillIdempotency create(Integer userId, String idemKey, String operation, Integer billId) {
        RecurringBillIdempotency r = new RecurringBillIdempotency();
        r.userId = userId;
        r.idemKey = idemKey;
        r.operation = operation;
        r.billId = billId;
        r.createdAt = OffsetDateTime.now();
        return r;
    }

    public Long getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getIdemKey() { return idemKey; }
    public String getOperation() { return operation; }
    public Integer getBillId() { return billId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setUserId(Integer userId) { this.userId = userId; }
    public void setIdemKey(String idemKey) { this.idemKey = idemKey; }
    public void setOperation(String operation) { this.operation = operation; }
    public void setBillId(Integer billId) { this.billId = billId; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
