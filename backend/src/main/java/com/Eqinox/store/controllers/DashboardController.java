package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;
import com.Eqinox.store.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthUserService authUserService;
    private final CategoryInitializationService categoryInitializationService;
    private final CategoryGoalRepository categoryGoalRepository;
    private final CategoryAvailableService availableService;
    private final BudgetItemRepository budgetItemRepo;
    private final BudgetService budgetService;
    private final WalletRepository walletRepository;
    private final WalletLedgerRepository walletLedgerRepository;

    public DashboardController(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            AuthUserService authUserService,
            CategoryInitializationService categoryInitializationService,
            CategoryGoalRepository categoryGoalRepository,
            CategoryAvailableService availableService,
            BudgetItemRepository budgetItemRepo,
            BudgetService budgetService,
            WalletRepository walletRepository,
            WalletLedgerRepository walletLedgerRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.authUserService = authUserService;
        this.categoryInitializationService = categoryInitializationService;
        this.categoryGoalRepository = categoryGoalRepository;
        this.availableService = availableService;
        this.budgetItemRepo = budgetItemRepo;
        this.budgetService = budgetService;
        this.walletRepository = walletRepository;
        this.walletLedgerRepository = walletLedgerRepository;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, List<Map<String, Object>>>>> getCategories(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int month,
            @RequestParam int year) {

        Integer userId = authUserService.getUserId(authHeader);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Initialize default categories (first login)
        if (categoryRepository.findByUserId(userId).isEmpty()) {
            categoryInitializationService.initializeUserCategories(user);
        }

        // Ensure budget period exists
        BudgetPeriod period = budgetService.getOrCreateBudget(userId, month, year);

        /* ============================
           INCOME + CARRY FORWARD LOGIC
        ============================ */

        BigDecimal income = BigDecimal.ZERO;

        Wallet wallet = walletRepository.findByUserId(userId).orElse(null);

        OffsetDateTime start =
                OffsetDateTime.of(year, month, 1, 0, 0, 0, 0,
                        OffsetDateTime.now().getOffset());

        OffsetDateTime end = start.plusMonths(1);

        if (wallet != null) {
            income = walletLedgerRepository.sumMonthlyWalletIncome(
                    wallet.getWalletId(),
                    TransferStatus.SUCCESS,
                    List.of(LedgerType.TRANSFER_IN, LedgerType.TOPUP),
                    start,
                    end
            );

            if (income == null) {
                income = BigDecimal.ZERO;
            }
        }

        BigDecimal carryForward =
                calculateCarryForward(userId, month, year);

        income = income.add(carryForward);

        // ✅ Persist correct income into period
        period.setIncome(income);
        budgetService.save(period);

        /* ============================
           ASSIGNED RECALC
        ============================ */

        BigDecimal assigned = budgetService.getAssignedTotal(period);
        period.setTotalAssigned(assigned);
        budgetService.save(period);

        List<Category> categories =
                categoryRepository.findByUserId(userId);

        Map<String, List<Map<String, Object>>> result =
                new LinkedHashMap<>();

        for (Category c : categories) {

            var goal = categoryGoalRepository
                    .findByCategoryIdAndMonthAndYear(
                            c.getCategoryId(), month, year)
                    .orElse(null);

            List<BudgetItem> items =
                    budgetItemRepo.findAllByPeriodAndCategory(
                            period, c.getCategoryId());

            BigDecimal planned =
                    items.isEmpty()
                            ? BigDecimal.ZERO
                            : items.get(0).getPlannedAmount();

            BigDecimal available =
                    availableService.calculateAvailable(
                            c.getCategoryId(),
                            month,
                            year);

            if (available == null) {
                available = BigDecimal.ZERO;
            }

            BigDecimal assignedAllTime =
                    budgetItemRepo.sumAssignedByCategoryUpTo(
                            c.getCategoryId(),
                            month,
                            year
                    );

            Map<String, Object> row = new HashMap<>();
            row.put("id", c.getCategoryId());
            row.put("name", c.getName());
            row.put("type", c.getType());

            // Budget values
            row.put("planned", planned);
            row.put("available", available);

            // Goal values
            row.put("target",
                    goal == null ? BigDecimal.ZERO : goal.getTargetAmount());
            row.put("frequency",
                    goal == null ? null : goal.getFrequency());
            row.put("totalTargetAmount",
                    goal == null ? null : goal.getTotalTargetAmount());
            row.put("totalAssignedAllTime", assignedAllTime);

            // Summary values
            row.put("monthIncome", income);
            row.put("assigned", assigned);
            row.put("remaining", income.subtract(assigned));

            result
                    .computeIfAbsent(c.getType(), k -> new ArrayList<>())
                    .add(row);
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /* ============================
       CARRY FORWARD CALCULATION
    ============================ */

    private BigDecimal calculateCarryForward(
            Integer userId,
            int month,
            int year
    ) {

        int prevMonth = month == 1 ? 12 : month - 1;
        int prevYear = month == 1 ? year - 1 : year;

        BudgetPeriod previous =
                budgetService.getBudgetIfExists(
                        userId, prevMonth, prevYear);

        if (previous == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal prevIncome =
                previous.getIncome() == null
                        ? BigDecimal.ZERO
                        : previous.getIncome();

        BigDecimal prevAssigned =
                previous.getTotalAssigned() == null
                        ? BigDecimal.ZERO
                        : previous.getTotalAssigned();

        BigDecimal leftover =
                prevIncome.subtract(prevAssigned);

        return leftover.max(BigDecimal.ZERO);
    }
}
