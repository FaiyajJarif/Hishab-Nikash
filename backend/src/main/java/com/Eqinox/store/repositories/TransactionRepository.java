package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Transaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.categoryId = :categoryId
            AND MONTH(t.date) = :month
            AND YEAR(t.date) = :year
      """)
  BigDecimal sumSpentForCategoryMonth(
      @Param("categoryId") Integer categoryId,
      @Param("month") int month,
      @Param("year") int year);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.categoryId = :categoryId
            AND (
                 YEAR(t.date) < :year
              OR (YEAR(t.date) = :year AND MONTH(t.date) <= :month)
            )
      """)
  BigDecimal sumSpentBeforeAndIncludingMonth(
      Integer categoryId,
      int month,
      int year);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND t.date = :date
      """)
  BigDecimal sumDailyExpense(
      @Param("userId") Integer userId,
      @Param("date") LocalDate date);

  @Query("""
          SELECT t.date, COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND EXTRACT(MONTH FROM t.date) = :month
            AND EXTRACT(YEAR FROM t.date) = :year
          GROUP BY t.date
          ORDER BY t.date
      """)
  List<Object[]> dailyTotalsForMonth(
      @Param("userId") Integer userId,
      @Param("month") int month,
      @Param("year") int year);

  @Query("""
          SELECT c.categoryId, c.name, COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          JOIN Category c ON c.categoryId = t.categoryId
          WHERE t.userId = :userId
            AND t.type = com.Eqinox.store.entities.TransactionType.EXPENSE
            AND t.date = :date
          GROUP BY c.categoryId, c.name
          ORDER BY COALESCE(SUM(t.amount), 0) DESC
      """)
  List<Object[]> dailyCategoryBreakdown(
      @Param("userId") Integer userId,
      @Param("date") LocalDate date);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0) / COUNT(DISTINCT t.date)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND t.date BETWEEN :start AND :end
      """)
  BigDecimal averageDailyExpense(
      @Param("userId") Integer userId,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND t.categoryId = :categoryId
            AND t.date = :date
      """)
  BigDecimal sumDailyCategoryExpense(
      @Param("userId") Integer userId,
      @Param("categoryId") Integer categoryId,
      @Param("date") LocalDate date);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0) / COUNT(DISTINCT t.date)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND t.categoryId = :categoryId
            AND t.date BETWEEN :start AND :end
      """)
  BigDecimal averageDailyCategoryExpense(
      @Param("userId") Integer userId,
      @Param("categoryId") Integer categoryId,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'INCOME'
            AND t.date = :date
      """)
  BigDecimal sumDailyIncome(
      Integer userId,
      LocalDate date);

  @Query("""
          SELECT t.categoryId, COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND t.date = :date
          GROUP BY t.categoryId
      """)
  List<Object[]> sumDailyExpenseByCategory(
      Integer userId,
      LocalDate date);

  @Query("""
          SELECT t.date, COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.type = 'EXPENSE'
            AND t.date BETWEEN :start AND :end
          GROUP BY t.date
          ORDER BY t.date
      """)
  List<Object[]> dailyExpenseForMonth(
      Integer userId,
      LocalDate start,
      LocalDate end);

  @Query("""
          select coalesce(avg(t.amount), 0)
          from Transaction t
          where t.userId = :userId
            and t.categoryId = :categoryId
            and t.type = com.Eqinox.store.entities.TransactionType.EXPENSE
            and t.date between :start and :end
      """)
  BigDecimal averageCategoryExpense(
      @Param("userId") Integer userId,
      @Param("categoryId") Integer categoryId,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

  @Query("""
          SELECT t
          FROM Transaction t
          WHERE t.userId = :userId
            AND t.date = :date
          ORDER BY t.id DESC
      """)
  List<Transaction> findAllForDay(
      @Param("userId") Integer userId,
      @Param("date") LocalDate date);

      @Query("select t from Transaction t where t.userId = :userId")
    java.util.List<Transaction> findRecentByUserId(@Param("userId") Integer userId, Pageable pageable);
}
