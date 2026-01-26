package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Transaction;
import com.Eqinox.store.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsTransactionRepository extends JpaRepository<Transaction, Integer> {

    // --------------------
    // DAILY
    // --------------------
    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.userId = :userId
          and t.date = :date
          and t.type = :type
    """)
    BigDecimal sumByDayAndType(
            @Param("userId") Integer userId,
            @Param("date") LocalDate date,
            @Param("type") TransactionType type
    );

    @Query("""
        select count(t)
        from Transaction t
        where t.userId = :userId
          and t.date = :date
    """)
    long countByDay(
            @Param("userId") Integer userId,
            @Param("date") LocalDate date
    );

    // --------------------
    // CALENDAR HEATMAP
    // --------------------
    @Query("""
        select t.date, coalesce(sum(t.amount), 0)
        from Transaction t
        where t.userId = :userId
          and t.date between :start and :end
          and t.type = com.Eqinox.store.entities.TransactionType.EXPENSE
        group by t.date
        order by t.date
    """)
    List<Object[]> dailyExpenseTotals(
            @Param("userId") Integer userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // --------------------
    // MONTH TOTALS
    // --------------------
    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.userId = :userId
          and t.date between :start and :end
          and t.type = :type
    """)
    BigDecimal sumByRangeAndType(
            @Param("userId") Integer userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("type") TransactionType type
    );

    // --------------------
    // CATEGORY BREAKDOWN
    // --------------------
    @Query("""
        select t.categoryId, coalesce(sum(t.amount), 0)
        from Transaction t
        where t.userId = :userId
          and t.date between :start and :end
          and t.type = com.Eqinox.store.entities.TransactionType.EXPENSE
        group by t.categoryId
    """)
    List<Object[]> monthExpenseByCategory(
            @Param("userId") Integer userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query(
        value = """
            SELECT AVG(t.amount)
            FROM transactions t
            JOIN users u ON u.id = t.user_id
            WHERE t.type = 'EXPENSE'
              AND EXTRACT(YEAR FROM AGE(u.date_of_birth)) BETWEEN :minAge AND :maxAge
              AND t.date BETWEEN :start AND :end
        """,
        nativeQuery = true
    )
    BigDecimal avgSpendingForAgeRange(
            @Param("minAge") int minAge,
            @Param("maxAge") int maxAge,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );    
}
