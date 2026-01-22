package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, Integer> {
    List<GoalContribution> findByUserIdAndMonthAndYear(Integer userId, Integer month, Integer year);
}
