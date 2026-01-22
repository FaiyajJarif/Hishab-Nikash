package com.Eqinox.store.services;

import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class MonthCloseService {

    private final BudgetPeriodRepository periodRepo;
    private final BudgetItemRepository itemRepo;
    private final CategoryRepository categoryRepo;

    public MonthCloseService(
            BudgetPeriodRepository periodRepo,
            BudgetItemRepository itemRepo,
            CategoryRepository categoryRepo
    ) {
        this.periodRepo = periodRepo;
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
    }

    public void closeMonth(Integer userId, int month, int year) {

        BudgetPeriod current = periodRepo
                .findByUserIdAndMonthAndYear(userId, month, year)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        BudgetPeriod next = periodRepo
                .findByUserIdAndMonthAndYear(userId, month + 1, year)
                .orElseGet(() -> {
                    BudgetPeriod p = new BudgetPeriod();
                    p.setUserId(userId);
                    p.setMonth(month + 1);
                    p.setYear(year);
                    p.setIncome(BigDecimal.ZERO);
                    p.setTotalAssigned(BigDecimal.ZERO);
                    return periodRepo.save(p);
                });

        List<BudgetItem> items = itemRepo.findByBudgetPeriod(current);
        List<Category> categories = categoryRepo.findByUserId(userId);

        for (BudgetItem item : items) {

            Category category = categories.stream()
                    .filter(c -> c.getCategoryId().equals(item.getCategoryId()))
                    .findFirst()
                    .orElse(null);

            if (category == null || !Boolean.TRUE.equals(category.getRolloverEnabled())) {
                continue;
            }

            BigDecimal planned = item.getPlannedAmount();
            BigDecimal actual = item.getActualAmount();
            BigDecimal leftover = planned.subtract(actual);

            if (leftover.compareTo(BigDecimal.ZERO) <= 0) continue;

            List<BudgetItem> nextItems =
        itemRepo.findAllByPeriodAndCategory(next, item.getCategoryId());

BudgetItem nextItem;

if (nextItems.isEmpty()) {
    BudgetItem ni = new BudgetItem();
    ni.setBudgetPeriod(next);
    ni.setCategoryId(item.getCategoryId());
    ni.setPlannedAmount(BigDecimal.ZERO);
    ni.setActualAmount(BigDecimal.ZERO);
    nextItem = itemRepo.save(ni);
} else {
    // invariant: there should be exactly one
    if (nextItems.size() > 1) {
        throw new IllegalStateException(
            "Data corruption: multiple budget items for period "
            + next.getBudgetId() + " and category " + item.getCategoryId()
        );
    }
    nextItem = nextItems.get(0);
}

                

            nextItem.setPlannedAmount(
                    nextItem.getPlannedAmount().add(leftover)
            );
            itemRepo.save(nextItem);
        }
    }
}
