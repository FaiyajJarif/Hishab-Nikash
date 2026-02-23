package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;   // ✅ REQUIRED

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    private String note;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    // ---------- getters ----------
    public Integer getTransactionId() { return transactionId; }
    public Integer getUserId() { return userId; }
    public Integer getCategoryId() { return categoryId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }

    // ---------- setters ----------
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
