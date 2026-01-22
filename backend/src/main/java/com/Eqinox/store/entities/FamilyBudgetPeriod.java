package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "family_budget_periods",
        uniqueConstraints = @UniqueConstraint(columnNames = {"familyId", "month", "year"})
)
public class FamilyBudgetPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer familyId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private BigDecimal income = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalAssigned = BigDecimal.ZERO;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFamilyId() { return familyId; }
    public void setFamilyId(Integer familyId) { this.familyId = familyId; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public BigDecimal getTotalAssigned() { return totalAssigned; }
    public void setTotalAssigned(BigDecimal totalAssigned) { this.totalAssigned = totalAssigned; }
}
