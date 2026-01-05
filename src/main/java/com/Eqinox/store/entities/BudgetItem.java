package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budget_items")
public class BudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "planned_amount", nullable = false)
    private BigDecimal plannedAmount = BigDecimal.ZERO;

    @Column(name = "actual_amount", nullable = false)
    private BigDecimal actualAmount = BigDecimal.ZERO;

    // ✅ SINGLE SOURCE OF TRUTH for budget_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private BudgetPeriod budgetPeriod;

    // ===== getters & setters =====

    public Integer getItemId() {
        return itemId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public BigDecimal getPlannedAmount() {
        return plannedAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public BudgetPeriod getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public void setBudgetPeriod(BudgetPeriod budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }
}
