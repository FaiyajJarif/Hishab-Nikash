package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetPeriodRepository
        extends JpaRepository<BudgetPeriod, Integer> {

    Optional<BudgetPeriod> findByUserIdAndMonthAndYear(
            Integer userId,
            Integer month,
            Integer year
    );
}

