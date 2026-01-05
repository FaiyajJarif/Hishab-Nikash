package com.Eqinox.store.services;

import com.Eqinox.store.entities.CategoryGoal;
import com.Eqinox.store.repositories.CategoryGoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryTargetService {

    private final CategoryGoalRepository repo;

    public CategoryTargetService(CategoryGoalRepository repo) {
        this.repo = repo;
    }

    public void saveTarget(
            Integer categoryId,
            Integer month,
            Integer year,
            BigDecimal amount,
            String frequency
    ) {

        BigDecimal monthlyAmount = switch (frequency) {
            case "DAILY" -> amount.multiply(BigDecimal.valueOf(30));
            case "WEEKLY" -> amount.multiply(BigDecimal.valueOf(4));
            case "YEARLY" -> amount.divide(BigDecimal.valueOf(12));
            default -> amount;
        };

        CategoryGoal goal = repo
                .findByCategoryIdAndMonthAndYear(categoryId, month, year)
                .orElse(new CategoryGoal());

        goal.setCategoryId(categoryId);
        goal.setMonth(month);
        goal.setYear(year);
        goal.setFrequency(frequency);
        goal.setTargetAmount(monthlyAmount);

        repo.save(goal);
    }
}
