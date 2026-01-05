package com.Eqinox.store.services;

import com.Eqinox.store.entities.Category;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.entities.UserOnboardingSelection;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.OnboardingSelectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryInitializationService {

    private final CategoryRepository categoryRepository;
    private final OnboardingSelectionRepository onboardingRepository;

    public CategoryInitializationService(
            CategoryRepository categoryRepository,
            OnboardingSelectionRepository onboardingRepository) {
        this.categoryRepository = categoryRepository;
        this.onboardingRepository = onboardingRepository;
    }

    /**
     * Called once when onboarding is completed
     */
    public void initializeUserCategories(User user) {

        Integer userId = user.getUserId();

        // ---- Default essentials ----
        createIfMissing(userId, "Groceries", "EXPENSE");
        createIfMissing(userId, "Transport", "EXPENSE");
        createIfMissing(userId, "Utilities", "EXPENSE");

        // ---- Read onboarding answers ----
        List<UserOnboardingSelection> selections =
                onboardingRepository.findByUserId(userId);

        for (UserOnboardingSelection s : selections) {

            String value = s.getValue().toLowerCase();

            // 🎵 Subscriptions
            if (s.getCategory().equalsIgnoreCase("subscription")) {
                createIfMissing(userId, capitalize(s.getValue()), "SUBSCRIPTION");
            }

            // 🎯 Goals
            if (s.getCategory().equalsIgnoreCase("goal")) {
                createIfMissing(userId,
                        capitalize(s.getValue()) + " Fund",
                        "GOAL");
            }

            // 🌿 Lifestyle
            if (s.getCategory().equalsIgnoreCase("lifestyle")) {
                createIfMissing(userId, capitalize(s.getValue()), "EXPENSE");
            }
        }
    }

    // ---------- helpers ----------

    private void createIfMissing(Integer userId, String name, String type) {
        if (!categoryRepository.existsByUserIdAndName(userId, name)) {
            categoryRepository.save(new Category(userId, name, type));
        }
    }

    private String capitalize(String v) {
        if (v == null || v.isBlank()) return v;
        return v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase();
    }
}
