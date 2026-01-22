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
    public BigDecimal calculateAvailable(
        Integer categoryId,
        int month,
        int year) {

    BigDecimal totalPlanned =
            budgetItemRepo.sumPlannedBeforeAndIncludingMonth(
                    categoryId, month, year
            );
            if (totalPlanned == null) {
                totalPlanned = BigDecimal.ZERO;
            }        
    BigDecimal totalSpent =
            transactionRepo.sumSpentBeforeAndIncludingMonth(
                    categoryId, month, year
            );
            if (totalSpent == null) {
                totalSpent = BigDecimal.ZERO;
            }

    return totalPlanned.subtract(totalSpent);
}

}

