package com.Eqinox.store.services;

import com.Eqinox.store.repositories.BudgetItemRepository;
import com.Eqinox.store.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryAvailableService {

    private final BudgetItemRepository budgetItemRepo;
    private final TransactionRepository transactionRepo;

    public CategoryAvailableService(
            BudgetItemRepository budgetItemRepo,
            TransactionRepository transactionRepo) {
        this.budgetItemRepo = budgetItemRepo;
        this.transactionRepo = transactionRepo;
    }

    /**
     * YNAB-style Available calculation
     */
    public BigDecimal calculateAvailable(
            Integer categoryId,
            int month,
            int year) {

        // 🔹 carry-over from previous months
        BigDecimal carryover =
                budgetItemRepo.sumCarryoverBeforeMonth(
                        categoryId, month, year
                );

        // 🔹 current month planned
        BigDecimal planned =
                budgetItemRepo.sumPlannedForCategoryMonth(
                        categoryId, month, year
                );

        // 🔹 current month spent
        BigDecimal spent =
                transactionRepo.sumSpentForCategoryMonth(
                        categoryId, month, year
                );

        return carryover
                .add(planned)
                .subtract(spent);
    }
}
