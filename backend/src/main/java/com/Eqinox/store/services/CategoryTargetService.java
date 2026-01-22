package com.Eqinox.store.services;

import com.Eqinox.store.dtos.CategoryTargetRequest;
import com.Eqinox.store.entities.CategoryGoal;
import com.Eqinox.store.repositories.CategoryGoalRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryTargetService {

    private final CategoryGoalRepository repo;

    public CategoryTargetService(CategoryGoalRepository repo) {
        this.repo = repo;
    }

    public void saveTarget(CategoryTargetRequest req) {

        CategoryGoal goal = repo
            .findByCategoryIdAndMonthAndYear(
                req.getCategoryId(),
                req.getMonth(),
                req.getYear()
            )
            .orElse(new CategoryGoal());

        goal.setCategoryId(req.getCategoryId());
        goal.setMonth(req.getMonth());
        goal.setYear(req.getYear());

        // frequency = MONTHLY or TOTAL
        goal.setFrequency(req.getFrequency());

        // monthly funding amount
        goal.setTargetAmount(req.getAmount());

        // total goal (ONLY for TOTAL)
        if ("TOTAL".equals(req.getFrequency())) {
            goal.setTotalTargetAmount(req.getTotalTargetAmount());
        } else {
            goal.setTotalTargetAmount(null);
        }

        repo.save(goal);
    }
}
