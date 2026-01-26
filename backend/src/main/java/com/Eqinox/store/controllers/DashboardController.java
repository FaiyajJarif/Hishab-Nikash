package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;
import com.Eqinox.store.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    public DashboardController(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            AuthUserService authUserService,
            CategoryInitializationService categoryInitializationService,
            CategoryGoalRepository categoryGoalRepository,
            CategoryAvailableService availableService,
            BudgetItemRepository budgetItemRepo,
            BudgetService budgetService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.authUserService = authUserService;
        this.categoryInitializationService = categoryInitializationService;
        this.categoryGoalRepository = categoryGoalRepository;
        this.availableService = availableService;
        this.budgetItemRepo = budgetItemRepo;
        this.budgetService = budgetService;
    }

    /**
     * DASHBOARD CATEGORY DATA
     * Used by Overview / Monthly / Assign Money / Category Drawer
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, List<Map<String, Object>>>>> getCategories(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int month,
            @RequestParam int year) {
        Integer userId = authUserService.getUserId(authHeader);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 Initialize default categories (first login)
        if (categoryRepository.findByUserId(userId).isEmpty()) {
            categoryInitializationService.initializeUserCategories(user);
        }

        // 🔹 Ensure budget period exists
        BudgetPeriod period = budgetService.getOrCreateBudget(userId, month, year);

        // 🔹 Null-safe totals (CRITICAL FIX)
        BigDecimal income = period.getIncome() == null ? BigDecimal.ZERO : period.getIncome();

        // 🔥 FORCE RECALC (CRITICAL)
        BigDecimal assigned = budgetService.getAssignedTotal(period);
        period.setTotalAssigned(assigned);
        budgetService.save(period);

        List<Category> categories = categoryRepository.findByUserId(userId);

        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();

        for (Category c : categories) {

            // 🎯 Goal (optional)
            var goal = categoryGoalRepository
                    .findByCategoryIdAndMonthAndYear(c.getCategoryId(), month, year)
                    .orElse(null);

            // 📊 Planned amount
            List<BudgetItem> items = budgetItemRepo.findAllByPeriodAndCategory(period, c.getCategoryId());

            BigDecimal planned = items.isEmpty()
                    ? BigDecimal.ZERO
                    : items.get(0).getPlannedAmount();

            // 💰 Available (safe)
            BigDecimal available = availableService.calculateAvailable(
                    c.getCategoryId(),
                    month,
                    year);
            if (available == null) {
                available = BigDecimal.ZERO;
            }

            BigDecimal totalAssignedForCategory =
            budgetItemRepo.sumAssignedForCategory(c.getCategoryId());

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

            // budget values
            row.put("planned", planned);
            row.put("available", available);

            // goal values
            row.put("target", goal == null ? BigDecimal.ZERO : goal.getTargetAmount());
            row.put("frequency", goal == null ? null : goal.getFrequency());
            row.put("totalTargetAmount",
    goal == null ? null : goal.getTotalTargetAmount());
            row.put("totalAssignedAllTime", assignedAllTime);


            row.put("totalAssignedAllTime", totalAssignedForCategory);

            // summary values (same for all rows – frontend expects this)
            row.put("monthIncome", income);
            row.put("assigned", assigned);
            row.put("remaining", income.subtract(assigned));

            result
                    .computeIfAbsent(c.getType(), k -> new ArrayList<>())
                    .add(row);
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
