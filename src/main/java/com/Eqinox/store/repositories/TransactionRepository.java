package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

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
            @Param("year") int year
    );
}
