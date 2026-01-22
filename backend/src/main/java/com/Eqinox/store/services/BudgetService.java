package com.Eqinox.store.services;

import com.Eqinox.store.dtos.DashboardSummaryDto;
import com.Eqinox.store.entities.BudgetItem;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.entities.Category;
import com.Eqinox.store.entities.FamilyMember;
import com.Eqinox.store.repositories.BudgetItemRepository;
import com.Eqinox.store.repositories.BudgetPeriodRepository;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.FamilyMemberRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BudgetService {

    private final BudgetPeriodRepository budgetPeriodRepo;
    private final BudgetItemRepository budgetItemRepo;
    private final CategoryRepository categoryRepo;
    private final FamilyNotificationService familyNotifier;
    private final FamilySyncService familySyncService;
    private final FamilyAuthorizationService familyAuthService;
    private final FamilyMemberRepository familyMemberRepo;

    public BudgetService(
            BudgetPeriodRepository budgetPeriodRepo,
            BudgetItemRepository budgetItemRepo,
            CategoryRepository categoryRepo,
            FamilyNotificationService familyNotifier,
            FamilySyncService familySyncService,
            FamilyAuthorizationService familyAuthService,
            FamilyMemberRepository familyMemberRepo) {
        this.budgetPeriodRepo = budgetPeriodRepo;
        this.budgetItemRepo = budgetItemRepo;
        this.categoryRepo = categoryRepo;
        this.familyNotifier = familyNotifier;
        this.familySyncService = familySyncService;
        this.familyAuthService = familyAuthService;
        this.familyMemberRepo = familyMemberRepo;
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
                    // ✅ RESET MONTHLY VALUES
                    p.setIncome(BigDecimal.ZERO);
                    p.setTotalAssigned(BigDecimal.ZERO);
                    p.setTotalPlanned(BigDecimal.ZERO);
                    p.setTotalActual(BigDecimal.ZERO);
                    return budgetPeriodRepo.save(p);
                });

        syncBudgetItems(period);

        return period;
    }

    // ✅ ensure items exist for every category
    public void syncBudgetItems(BudgetPeriod period) {
        List<Category> categories = categoryRepo.findByUserId(period.getUserId());

        for (Category c : categories) {
            List<BudgetItem> items =
                budgetItemRepo.findAllByPeriodAndCategory(period, c.getCategoryId());
                BudgetItem item;
                if (items.isEmpty()) {
                    item = new BudgetItem();
                    item.setBudgetPeriod(period);
                    item.setCategoryId(c.getCategoryId());
                    item.setPlannedAmount(BigDecimal.ZERO);
                    item.setActualAmount(BigDecimal.ZERO);
                    item = budgetItemRepo.save(item);
                } else {
                    item = items.get(0); // 👈 ALWAYS PICK FIRST
                }
        }
    }

    // ✅ plan money
    public void planAmount(
            Integer budgetPeriodId,
            Integer categoryId,
            BigDecimal amount,
            Integer userId) {
        // added validation
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Planned amount cannot be negative");
        }

        List<FamilyMember> memberships =
            familyMemberRepo.findByUserId(userId);

        familyAuthService.authorizeBudgetEdit(memberships);
        BudgetPeriod period = budgetPeriodRepo.findById(budgetPeriodId).orElseThrow();

        syncBudgetItems(period); // 🔴 VERY IMPORTANT

            List<BudgetItem> items =
                budgetItemRepo.findAllByPeriodAndCategory(period, categoryId);
                BudgetItem item;
                if (items.isEmpty()) {
                    item = new BudgetItem();
                    item.setBudgetPeriod(period);
                    item.setCategoryId(categoryId);
                    item.setPlannedAmount(BigDecimal.ZERO);
                    item.setActualAmount(BigDecimal.ZERO);
                    item = budgetItemRepo.save(item);
                } else {
                    item = items.get(0); // 👈 ALWAYS PICK FIRST
                }

        BigDecimal previous = item.getPlannedAmount() == null
                ? BigDecimal.ZERO
                : item.getPlannedAmount();

        BigDecimal newAmount = previous.add(amount);
        BigDecimal delta = amount.subtract(previous);

        BigDecimal assigned = getAssignedTotal(period);
        BigDecimal remaining = period.getIncome().subtract(assigned);

        if (delta.compareTo(remaining) > 0) {
            throw new IllegalStateException("Not enough money to assign");
        }

        item.setPlannedAmount(newAmount);
        budgetItemRepo.save(item);

        BigDecimal newAssigned = budgetItemRepo.sumPlannedForPeriod(period);
        period.setTotalAssigned(newAssigned);
        budgetPeriodRepo.save(period);

        familySyncService.broadcastUserChange(
                userId,
                "BUDGET_UPDATED",
                "📊 Budget updated by a family member");

    }

    public BigDecimal getUnassignedIncome(BudgetPeriod period) {
        return period.getIncome()
                .subtract(period.getTotalAssigned());
    }

    // ✅ recalc totals
    private void recalcTotals(BudgetPeriod period) {
        BigDecimal planned = budgetItemRepo.sumPlannedForPeriod(period);

        BigDecimal actual = budgetItemRepo.sumActualForPeriod(period);

        period.setTotalPlanned(planned);
        period.setTotalActual(actual);

        budgetPeriodRepo.save(period);
    }

    public void save(BudgetPeriod period) {
        budgetPeriodRepo.save(period);
    }

    public BigDecimal getAssignedTotal(BudgetPeriod period) {
        BigDecimal assigned = budgetItemRepo.sumPlannedForPeriod(period);
        return assigned == null ? BigDecimal.ZERO : assigned;
    }

    public DashboardSummaryDto getSummary(Integer userId, Integer month, Integer year) {

        BudgetPeriod period = getOrCreateBudget(userId, month, year);
    
        BigDecimal assigned = getAssignedTotal(period);
        BigDecimal remaining = period.getIncome().subtract(assigned);
    
        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setIncome(period.getIncome());
        dto.setAssigned(assigned);
        dto.setRemaining(remaining);

    
        return dto;
    }    

    public void saveIncome(BudgetPeriod period, BigDecimal income) {

        if (income == null) {
            throw new IllegalArgumentException("Income cannot be null");
        }

        if (income.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Income cannot be negative");
        }

        period.setIncome(income);
        budgetPeriodRepo.save(period);
    }

    // ✅ ADD income (cumulative)
    public void addIncome(BudgetPeriod period, BigDecimal amount) {

        if (amount == null) {
            throw new IllegalArgumentException("Income cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income must be positive");
        }

        BigDecimal current = period.getIncome() == null
                ? BigDecimal.ZERO
                : period.getIncome();

        period.setIncome(current.add(amount));
        budgetPeriodRepo.save(period);
    }
    public void moveMoney(
        BudgetPeriod period,
        Integer fromCategory,
        Integer toCategory,
        BigDecimal amount
    ) {
        BudgetItem from = budgetItemRepo
            .findByBudgetPeriodAndCategoryId(period, fromCategory)
            .orElseThrow(() -> new IllegalStateException("Source category not found"));
    
        BudgetItem to = budgetItemRepo
            .findByBudgetPeriodAndCategoryId(period, toCategory)
            .orElseThrow(() -> new IllegalStateException("Target category not found"));
    
        if (from.getPlannedAmount().compareTo(amount) < 0) {
            throw new IllegalStateException("Not enough money to move");
        }
    
        from.setPlannedAmount(from.getPlannedAmount().subtract(amount));
        to.setPlannedAmount(to.getPlannedAmount().add(amount));
    
        budgetItemRepo.save(from);
        budgetItemRepo.save(to);
    }    
}
