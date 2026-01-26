package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "monthly_analytics")
public class MonthlyAnalyticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private BigDecimal income;

    @Column(nullable = false)
    private BigDecimal expense;

    @Column(nullable = false)
    private BigDecimal assigned;

    @Column(nullable = false)
    private BigDecimal unassigned;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    private OffsetDateTime closedAt;

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }

    public BigDecimal getAssigned() { return assigned; }
    public void setAssigned(BigDecimal assigned) { this.assigned = assigned; }

    public BigDecimal getUnassigned() { return unassigned; }
    public void setUnassigned(BigDecimal unassigned) { this.unassigned = unassigned; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getClosedAt() {
        return closedAt;
    }
    
    public void setClosedAt(OffsetDateTime closedAt) {
        this.closedAt = closedAt;
    }    
}
