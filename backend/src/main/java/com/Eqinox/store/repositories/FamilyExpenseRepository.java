package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.FamilyExpense;
import com.Eqinox.store.repositories.projections.FamilyExpenseView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FamilyExpenseRepository
        extends JpaRepository<FamilyExpense, Integer> {
    List<FamilyExpense> findByFamilyIdOrderBySpentAtDesc(Integer familyId);
    @Query(value = """
        SELECT
            e.id        AS "id",
            e.amount    AS "amount",
            e.note      AS "note",
            e.spent_at  AS "spentAt",
            c.name      AS "categoryName",
            u.name      AS "userName",
            f.name      AS "familyName"
        FROM family_expense e
        JOIN family_categories c ON c.id = e.category_id
        JOIN "users" u ON u.id = e.user_id
        JOIN family_groups f ON f.group_id = e.family_id
        WHERE e.family_id = :familyId
          AND e.spent_at >= :from
        ORDER BY e.spent_at DESC
        """, nativeQuery = true)
    List<FamilyExpenseView> findRecentExpenses(
        @Param("familyId") Integer familyId,
        @Param("from") LocalDateTime from
    );

    @Query(
        value = """
        SELECT
          c.name AS categoryName,
          SUM(e.amount) AS total
        FROM family_expense e
        JOIN family_categories c ON c.id = e.category_id
        WHERE e.family_id = :familyId
          AND EXTRACT(MONTH FROM e.spent_at) = :month
          AND EXTRACT(YEAR FROM e.spent_at) = :year
        GROUP BY c.name
        ORDER BY total DESC
        """,
        nativeQuery = true
    )
    List<Map<String, Object>> getMonthlyExpenseSummary(
        @Param("familyId") Integer familyId,
        @Param("month") int month,
        @Param("year") int year
    );    

}
