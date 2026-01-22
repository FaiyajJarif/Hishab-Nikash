package com.Eqinox.store.services;

import com.Eqinox.store.entities.FamilyBudgetItem;
import com.Eqinox.store.entities.FamilyBudgetPeriod;
import com.Eqinox.store.entities.FamilyCategory;
import com.Eqinox.store.repositories.FamilyBudgetItemRepository;
import com.Eqinox.store.repositories.FamilyBudgetPeriodRepository;
import com.Eqinox.store.repositories.FamilyCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class FamilyBudgetService {

    private final FamilyBudgetPeriodRepository periodRepo;
    private final FamilyBudgetItemRepository itemRepo;
    private final FamilyCategoryRepository categoryRepo;
    private final FamilySyncService familySyncService;

    public FamilyBudgetService(
            FamilyBudgetPeriodRepository periodRepo,
            FamilyBudgetItemRepository itemRepo,
            FamilyCategoryRepository categoryRepo,
            FamilySyncService familySyncService
    ) {
        this.periodRepo = periodRepo;
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
        this.familySyncService = familySyncService;
    }

    public FamilyBudgetPeriod getOrCreatePeriod(Integer familyId, Integer month, Integer year) {
        validateMonthYear(month, year);

        return periodRepo.findByFamilyIdAndMonthAndYear(familyId, month, year)
                .orElseGet(() -> {
                    FamilyBudgetPeriod p = new FamilyBudgetPeriod();
                    p.setFamilyId(familyId);
                    p.setMonth(month);
                    p.setYear(year);
                    p.setIncome(BigDecimal.ZERO);
                    p.setTotalAssigned(BigDecimal.ZERO);
                    return periodRepo.save(p);
                });
    }

    public void syncItems(Integer familyId, FamilyBudgetPeriod period) {
        List<FamilyCategory> cats = categoryRepo.findByFamilyId(familyId);

        for (FamilyCategory c : cats) {
            itemRepo.findByPeriodIdAndCategoryId(period.getId(), c.getId())
                    .orElseGet(() -> {
                        FamilyBudgetItem i = new FamilyBudgetItem();
                        i.setPeriodId(period.getId());
                        i.setCategoryId(c.getId());
                        i.setPlannedAmount(BigDecimal.ZERO);
                        i.setActualAmount(BigDecimal.ZERO);
                        return itemRepo.save(i);
                    });
        }
    }

    public void setIncome(Integer familyId, Integer month, Integer year, BigDecimal income, Integer actorUserId) {
        validateNonNegative(income, "Income");

        FamilyBudgetPeriod p = getOrCreatePeriod(familyId, month, year);
        syncItems(familyId, p);

        p.setIncome(income);
        periodRepo.save(p);

        familySyncService.broadcastFamilyEvent(
                familyId,
                actorUserId,
                "FAMILY_INCOME_UPDATED",
                "💰 Family income updated"
        );
    }

    public void plan(Integer familyId, Integer month, Integer year, Integer categoryId, BigDecimal amount, Integer actorUserId) {
        validateNonNegative(amount, "Planned amount");

        FamilyBudgetPeriod p = getOrCreatePeriod(familyId, month, year);
        syncItems(familyId, p);

        FamilyBudgetItem item = itemRepo.findByPeriodIdAndCategoryId(p.getId(), categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found in this period"));

        BigDecimal prev = item.getPlannedAmount() == null ? BigDecimal.ZERO : item.getPlannedAmount();
        BigDecimal delta = amount.subtract(prev);

        BigDecimal assigned = itemRepo.sumPlanned(p.getId());
        BigDecimal remaining = p.getIncome().subtract(assigned);

        if (delta.compareTo(remaining) > 0) {
            throw new IllegalStateException("Not enough remaining family income to assign");
        }

        item.setPlannedAmount(amount);
        itemRepo.save(item);

        p.setTotalAssigned(itemRepo.sumPlanned(p.getId()));
        periodRepo.save(p);

        familySyncService.broadcastFamilyEvent(
                familyId,
                actorUserId,
                "FAMILY_BUDGET_PLANNED",
                "📊 Family budget plan updated"
        );
    }

    public BigDecimal getAssigned(Integer periodId) {
        return itemRepo.sumPlanned(periodId);
    }

    public List<FamilyBudgetItem> getItems(Integer periodId) {
        return itemRepo.findByPeriodId(periodId);
    }

    private void validateNonNegative(BigDecimal v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " is required");
        if (v.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException(field + " cannot be negative");
    }

    private void validateMonthYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) throw new IllegalArgumentException("Invalid month");
        if (year == null || year < 2000 || year > 2100) throw new IllegalArgumentException("Invalid year");
    }
}
