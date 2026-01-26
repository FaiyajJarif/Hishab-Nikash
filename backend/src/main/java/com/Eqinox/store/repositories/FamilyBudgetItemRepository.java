package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.FamilyBudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FamilyBudgetItemRepository extends JpaRepository<FamilyBudgetItem, Integer> {

    List<FamilyBudgetItem> findByPeriodId(Integer periodId);

    Optional<FamilyBudgetItem> findByPeriodIdAndCategoryId(Integer periodId, Integer categoryId);

    @Query("select coalesce(sum(i.plannedAmount), 0) from FamilyBudgetItem i where i.periodId = :periodId")
    BigDecimal sumPlanned(Integer periodId);

    @Query("""
        SELECT new map(
        c.id as categoryId,
        c.name as categoryName,
        i.plannedAmount as plannedAmount,
        i.actualAmount as actualAmount
        )
        FROM FamilyBudgetItem i
        JOIN FamilyCategory c ON c.id = i.categoryId
        WHERE i.periodId = :periodId
        """)
        List<Map<String, Object>> findDetailedByPeriodId(Integer periodId);

}
