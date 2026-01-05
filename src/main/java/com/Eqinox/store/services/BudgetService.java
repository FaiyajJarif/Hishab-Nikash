package com.Eqinox.store.services;

import com.Eqinox.store.entities.BudgetItem;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.entities.Category;
import com.Eqinox.store.repositories.BudgetItemRepository;
import com.Eqinox.store.repositories.BudgetPeriodRepository;
import com.Eqinox.store.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetPeriodRepository budgetPeriodRepo;
    private final BudgetItemRepository budgetItemRepo;
    private final CategoryRepository categoryRepo;

    public BudgetService(
            BudgetPeriodRepository budgetPeriodRepo,
            BudgetItemRepository budgetItemRepo,
            CategoryRepository categoryRepo) {
        this.budgetPeriodRepo = budgetPeriodRepo;
        this.budgetItemRepo = budgetItemRepo;
        this.categoryRepo = categoryRepo;
    }

    // ✅ current month
    public BudgetPeriod getOrCreateCurrentBudget(Integer userId) {
        LocalDate now = LocalDate.now();
        return getOrCreateBudget(userId, now.getMonthValue(), now.getYear());
    }

    // ✅ any month
    public BudgetPeriod getOrCreateBudget(Integer userId, Integer month, Integer year) {

        BudgetPeriod period = budgetPeriodRepo
                .findByUserIdAndMonthAndYear(userId, month, year)
                .orElseGet(() -> {
                    BudgetPeriod p = new BudgetPeriod();
                    p.setUserId(userId);
                    p.setMonth(month);
                    p.setYear(year);
                    return budgetPeriodRepo.save(p);
                });

        syncBudgetItems(period);

        return period;
    }

    // ✅ ensure items exist for every category
    public void syncBudgetItems(BudgetPeriod period) {
        List<Category> categories = categoryRepo.findByUserId(period.getUserId());

        for (Category c : categories) {
            budgetItemRepo
                    .findByBudgetPeriodAndCategoryId(period, c.getCategoryId())
                    .orElseGet(() -> {
                        BudgetItem item = new BudgetItem();
                        item.setBudgetPeriod(period); // ✅ FIX
                        item.setCategoryId(c.getCategoryId());
                        return budgetItemRepo.save(item);
                    });
        }
    }

    // ✅ plan money
    public void planAmount(
            Integer budgetPeriodId,
            Integer categoryId,
            BigDecimal amount) {

        BudgetPeriod period = budgetPeriodRepo.findById(budgetPeriodId).orElseThrow();

        BudgetItem item = budgetItemRepo
                .findByBudgetPeriodAndCategoryId(period, categoryId)
                .orElseThrow();

        item.setPlannedAmount(amount);
        budgetItemRepo.save(item);

        recalcTotals(period);
    }

    // ✅ recalc totals
    private void recalcTotals(BudgetPeriod period) {
        BigDecimal planned = budgetItemRepo.sumPlannedForPeriod(period);

        BigDecimal actual = budgetItemRepo.sumActualForPeriod(period);

        period.setTotalPlanned(planned);
        period.setTotalActual(actual);

        budgetPeriodRepo.save(period);
    }
}
