package com.Eqinox.store.services;

import com.Eqinox.store.dtos.CategorySpendingAnomalyDto;
import com.Eqinox.store.dtos.analytics.CategoryAnomalyDto;
import com.Eqinox.store.entities.Category;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryAnomalyService {

    private final TransactionRepository transactionRepo;
    private final CategoryRepository categoryRepo;

    private static final BigDecimal THRESHOLD = new BigDecimal("1.5");

    public CategoryAnomalyService(
            TransactionRepository transactionRepo,
            CategoryRepository categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<CategorySpendingAnomalyDto> detect(
            Integer userId,
            LocalDate date) {

        LocalDate start = date.minusDays(30);
        LocalDate end = date.minusDays(1);

        List<CategorySpendingAnomalyDto> result = new ArrayList<>();

        for (Category cat : categoryRepo.findAll()) {

            BigDecimal today =
                    transactionRepo.sumDailyCategoryExpense(
                            userId, cat.getUserId(), date);

            if (today.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal avg =
                    transactionRepo.averageDailyCategoryExpense(
                            userId, cat.getUserId(), start, end);

            if (avg == null || avg.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal ratio =
                    today.divide(avg, 2, RoundingMode.HALF_UP);

            if (ratio.compareTo(THRESHOLD) < 0) continue;

            BigDecimal percent =
                    ratio.subtract(BigDecimal.ONE)
                         .multiply(new BigDecimal("100"))
                         .setScale(0, RoundingMode.HALF_UP);

            CategorySpendingAnomalyDto dto =
                    new CategorySpendingAnomalyDto();

            dto.setCategoryId(cat.getUserId());
            dto.setCategoryName(cat.getName());
            dto.setDate(date);
            dto.setTodaySpend(today);
            dto.setAverageSpend(avg);
            dto.setPercentageIncrease(percent);

            result.add(dto);
        }

        return result;
    }

    public List<CategoryAnomalyDto> detectDailyAnomalies(
            Integer userId,
            LocalDate date) {

        LocalDate start = date.minusDays(14);
        List<Category> categories = categoryRepo.findByUserId(userId);
        List<CategoryAnomalyDto> alerts = new ArrayList<>();

        for (Category c : categories) {
            BigDecimal today = transactionRepo.sumDailyCategoryExpense(
                    userId, c.getCategoryId(), date);

            if (today.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal avg = transactionRepo.averageCategoryExpense(
                    userId, c.getCategoryId(), start, date.minusDays(1));

            if (avg.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal ratio = today.divide(avg, 2, BigDecimal.ROUND_HALF_UP);

            if (ratio.compareTo(THRESHOLD) >= 0) {
                alerts.add(new CategoryAnomalyDto(
                        c.getCategoryId(),
                        c.getName(),
                        date,
                        today,
                        avg,
                        ratio
                ));
            }
        }
        return alerts;
    }
}
