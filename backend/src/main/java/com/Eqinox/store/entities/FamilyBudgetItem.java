package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "family_budget_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"periodId", "categoryId"})
)
public class FamilyBudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer periodId;

    @Column(nullable = false)
    private Integer categoryId;

    @Column(nullable = false)
    private BigDecimal plannedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal actualAmount = BigDecimal.ZERO;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPeriodId() { return periodId; }
    public void setPeriodId(Integer periodId) { this.periodId = periodId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount; }

    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }
}
