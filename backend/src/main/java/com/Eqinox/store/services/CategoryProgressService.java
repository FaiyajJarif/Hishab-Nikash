package com.Eqinox.store.services;

import com.Eqinox.store.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryProgressService {

    private final TransactionRepository transactionRepository;

    public CategoryProgressService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public int calculateProgress(
            Integer categoryId,
            BigDecimal target,
            int month,
            int year) {
        // No target → no progress bar
        if (target == null || target.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal spent = transactionRepository
                .sumSpentForCategoryMonth(categoryId, month, year);

        if (spent == null || spent.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        return spent
                .multiply(BigDecimal.valueOf(100))
                .divide(target, 0, BigDecimal.ROUND_HALF_UP)
                .intValue();
    }

}
