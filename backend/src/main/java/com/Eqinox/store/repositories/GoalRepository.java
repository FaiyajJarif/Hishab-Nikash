package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
    List<Goal> findByUserIdAndActiveTrue(Integer userId);
    Optional<Goal> findByGoalIdAndUserId(Integer goalId, Integer userId);
}
