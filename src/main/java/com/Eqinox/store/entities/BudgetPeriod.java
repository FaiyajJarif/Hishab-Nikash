package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budget_periods")
public class BudgetPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Integer budgetId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    private Integer month;
    private Integer year;

    @Column(name = "total_planned")
    private BigDecimal totalPlanned = BigDecimal.ZERO;

    @Column(name = "total_actual")
    private BigDecimal totalActual = BigDecimal.ZERO;

    // Getters & Setters
    public Integer getBudgetId() { return budgetId; }
    public void setBudgetId(Integer budgetId) { this.budgetId = budgetId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getTotalPlanned() { return totalPlanned; }
    public void setTotalPlanned(BigDecimal totalPlanned) { this.totalPlanned = totalPlanned; }

    public BigDecimal getTotalActual() { return totalActual; }
    public void setTotalActual(BigDecimal totalActual) { this.totalActual = totalActual; }
}
