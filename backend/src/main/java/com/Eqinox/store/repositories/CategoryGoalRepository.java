package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.CategoryGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryGoalRepository
        extends JpaRepository<CategoryGoal, Integer> {

    Optional<CategoryGoal> findByCategoryIdAndMonthAndYear(
            Integer categoryId,
            Integer month,
            Integer year
    );
}
