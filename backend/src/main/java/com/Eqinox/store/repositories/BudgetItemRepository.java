package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.BudgetItem;
import com.Eqinox.store.entities.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Integer> {

    @Query("""
        SELECT b FROM BudgetItem b
        WHERE b.budgetPeriod = :period
          AND b.categoryId = :categoryId
      """)
      List<BudgetItem> findAllByPeriodAndCategory(
          BudgetPeriod period,
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
      @Param("year") int year);

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
      @Param("year") int year);

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

  @Query("""
          SELECT COALESCE(SUM(b.plannedAmount), 0)
          FROM BudgetItem b
          WHERE b.categoryId = :categoryId
            AND (
                 b.budgetPeriod.year < :year
              OR (b.budgetPeriod.year = :year AND b.budgetPeriod.month <= :month)
            )
      """)
  BigDecimal sumPlannedBeforeAndIncludingMonth(
      Integer categoryId,
      int month,
      int year);

  List<BudgetItem> findByBudgetPeriod(BudgetPeriod budgetPeriod);

  @Modifying
  @Query("""
          UPDATE BudgetItem b
          SET b.actualAmount = COALESCE(b.actualAmount, 0) + :amount
          WHERE b.categoryId = :categoryId
            AND b.budgetPeriod.userId = :userId
            AND b.budgetPeriod.month = :month
            AND b.budgetPeriod.year = :year
      """)
  void addActualAmount(
      Integer userId,
      Integer categoryId,
      int month,
      int year,
      BigDecimal amount);
      @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.categoryId = :categoryId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
      """)
      BigDecimal sumSpentForCategoryMonth(
          Integer categoryId,
          int month,
          int year
      );   
      
      Optional<BudgetItem> findByBudgetPeriodAndCategoryId(
        BudgetPeriod budgetPeriod,
        Integer categoryId
    );    

    @Query("""
        SELECT COALESCE(SUM(b.plannedAmount), 0)
        FROM BudgetItem b
        WHERE b.categoryId = :categoryId
        """)
        BigDecimal sumAssignedForCategory(@Param("categoryId") Integer categoryId);

        @Query("""
        SELECT COALESCE(SUM(b.plannedAmount), 0)
        FROM BudgetItem b
        WHERE b.categoryId = :categoryId
            AND (b.budgetPeriod.year < :year
            OR (b.budgetPeriod.year = :year AND b.budgetPeriod.month <= :month))
        """)
        BigDecimal sumAssignedByCategoryUpTo(
            Integer categoryId,
            Integer month,
            Integer year
        );
}
