package com.Eqinox.store.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "recurring_bill_runs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bill_id", "due_date"})
)
public class RecurringBillRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", nullable = false)
    private Integer billId;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt = OffsetDateTime.now();

    public Long getId() { return id; }

    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public OffsetDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; }
}
