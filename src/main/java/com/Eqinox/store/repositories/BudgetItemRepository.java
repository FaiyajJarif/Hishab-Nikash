package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.BudgetItem;
import com.Eqinox.store.entities.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Integer> {

    Optional<BudgetItem> findByBudgetPeriodAndCategoryId(
            BudgetPeriod budgetPeriod,
            Integer categoryId
    );

    // 🔹 total planned for a category in a month
    @Query("""
        SELECT COALESCE(SUM(b.plannedAmount), 0)
        FROM BudgetItem b
        WHERE b.categoryId = :categoryId
          AND b.budgetPeriod.month = :month
          AND b.budgetPeriod.year = :year
    """)
    BigDecimal sumPlannedForCategoryMonth(
            @Param("categoryId") Integer categoryId,
            @Param("month") int month,
            @Param("year") int year
    );

    // 🔹 carry-over calculation (everything before this month)
    @Query("""
        SELECT COALESCE(SUM(b.plannedAmount - b.actualAmount), 0)
        FROM BudgetItem b
        WHERE b.categoryId = :categoryId
          AND (
               b.budgetPeriod.year < :year
            OR (b.budgetPeriod.year = :year AND b.budgetPeriod.month < :month)
          )
    """)
    BigDecimal sumCarryoverBeforeMonth(
            @Param("categoryId") Integer categoryId,
            @Param("month") int month,
            @Param("year") int year
    );

    // 🔹 totals for dashboard header
    @Query("""
        SELECT COALESCE(SUM(b.plannedAmount), 0)
        FROM BudgetItem b
        WHERE b.budgetPeriod = :period
    """)
    BigDecimal sumPlannedForPeriod(@Param("period") BudgetPeriod period);

    @Query("""
        SELECT COALESCE(SUM(b.actualAmount), 0)
        FROM BudgetItem b
        WHERE b.budgetPeriod = :period
    """)
    BigDecimal sumActualForPeriod(@Param("period") BudgetPeriod period);
}
