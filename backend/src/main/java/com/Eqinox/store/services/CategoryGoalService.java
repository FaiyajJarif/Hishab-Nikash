package com.Eqinox.store.services;

import com.Eqinox.store.entities.Category;
import com.Eqinox.store.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryGoalService {

    private final CategoryRepository categoryRepository;

    public CategoryGoalService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void setGoal(
            Integer userId,
            Integer categoryId,
            Boolean isGoal,
            BigDecimal amount,
            String frequency
    ) {
        Category category =
                categoryRepository
                        .findByCategoryIdAndUserId(categoryId, userId)
                        .orElseThrow();

        category.setIsGoal(isGoal);
        category.setGoalAmount(amount);
        category.setGoalFrequency(frequency);

        categoryRepository.save(category);
    }
}
