package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.FamilyBudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyBudgetPeriodRepository extends JpaRepository<FamilyBudgetPeriod, Integer> {
    Optional<FamilyBudgetPeriod> findByFamilyIdAndMonthAndYear(Integer familyId, Integer month, Integer year);
}
