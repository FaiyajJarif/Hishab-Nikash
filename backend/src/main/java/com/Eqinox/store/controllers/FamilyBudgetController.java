package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.CreateFamilyCategoryRequest;
import com.Eqinox.store.dtos.FamilyIncomeRequest;
import com.Eqinox.store.dtos.FamilyPlanRequest;
import com.Eqinox.store.dtos.FamilySpendRequest;
import com.Eqinox.store.dtos.RenameCategoryRequest;
import com.Eqinox.store.entities.FamilyBudgetPeriod;
import com.Eqinox.store.entities.FamilyCategory;
import com.Eqinox.store.entities.FamilyExpense;
import com.Eqinox.store.repositories.FamilyCategoryRepository;
import com.Eqinox.store.repositories.FamilyExpenseRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.FamilyAuthorizationService;
import com.Eqinox.store.services.FamilyBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.Eqinox.store.services.FamilySyncService;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/family/{familyId}/budget")
public class FamilyBudgetController {

    private final JwtService jwtService;
    private final FamilyAuthorizationService familyAuth;
    private final FamilyBudgetService familyBudgetService;
    private final FamilyCategoryRepository familyCategoryRepo;
    private final FamilySyncService familySyncService;
    private final FamilyExpenseRepository expenseRepo;

    public FamilyBudgetController(
            JwtService jwtService,
            FamilyAuthorizationService familyAuth,
            FamilyBudgetService familyBudgetService,
            FamilyCategoryRepository familyCategoryRepo,
            FamilySyncService familySyncService,
            FamilyExpenseRepository expenseRepo
    ) {
        this.jwtService = jwtService;
        this.familyAuth = familyAuth;
        this.familyBudgetService = familyBudgetService;
        this.familyCategoryRepo = familyCategoryRepo;
        this.familySyncService = familySyncService;
        this.expenseRepo = expenseRepo;
    }

    private Integer userIdFromAuth(String auth) {
        return jwtService.extractUserId(auth.substring(7));
    }

    // ✅ Create family category
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @RequestBody CreateFamilyCategoryRequest req
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyEdit(familyId, userId);

        if (req.getName() == null || req.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Category name required");
        }

        FamilyCategory c = new FamilyCategory();
        c.setFamilyId(familyId);
        c.setName(req.getName());
        c.setType(req.getType() == null ? "Other" : req.getType());

        return ResponseEntity.ok(familyCategoryRepo.save(c));
    }

    // ✅ List family categories
    @GetMapping("/categories")
    public ResponseEntity<?> categories(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyView(familyId, userId);

        return ResponseEntity.ok(
                familyCategoryRepo.findByFamilyId(familyId)
        );
    }

    // ✅ Set family income
    @PostMapping("/income")
    public ResponseEntity<?> setIncome(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @RequestBody FamilyIncomeRequest req
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyEdit(familyId, userId);

        familyBudgetService.setIncome(familyId, req.getMonth(), req.getYear(), req.getIncome(), userId);
        return ResponseEntity.ok().build();
    }

    // ✅ Plan category
    @PostMapping("/plan")
    public ResponseEntity<?> plan(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @RequestBody FamilyPlanRequest req
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyEdit(familyId, userId);

        familyBudgetService.plan(familyId, req.getMonth(), req.getYear(), req.getCategoryId(), req.getAmount(), userId);
        return ResponseEntity.ok().build();
    }

    // ✅ Summary
    @GetMapping("/summary")
    public ResponseEntity<?> summary(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyView(familyId, userId);

        FamilyBudgetPeriod p = familyBudgetService.getOrCreatePeriod(familyId, month, year);
        familyBudgetService.syncItems(familyId, p);

        BigDecimal assigned = familyBudgetService.getAssigned(p.getId());
        BigDecimal remaining = p.getIncome().subtract(assigned);

        Map<String, Object> res = new HashMap<>();
        res.put("income", p.getIncome());
        res.put("assigned", assigned);
        res.put("remaining", remaining);

        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    @GetMapping("/items")
    public ResponseEntity<?> budgetItems(
        @RequestHeader("Authorization") String auth,
        @PathVariable Integer familyId,
        @RequestParam Integer month,
        @RequestParam Integer year
    ) {
    Integer userId = userIdFromAuth(auth);
    familyAuth.authorizeFamilyView(familyId, userId);

    FamilyBudgetPeriod p =
            familyBudgetService.getOrCreatePeriod(familyId, month, year);

    familyBudgetService.syncItems(familyId, p);

    return ResponseEntity.ok(
        familyBudgetService.getItemsWithCategory(p.getId())
    );
}
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> renameCategory(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @PathVariable Integer categoryId,
            @RequestBody RenameCategoryRequest req
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyEdit(familyId, userId);

        if (req.name() == null || req.name().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        FamilyCategory category = familyCategoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Category does not belong to this family");
        }

        String oldName = category.getName();
        category.setName(req.name().trim());
        familyCategoryRepo.save(category);

        // 🔔 WS EVENT
        familySyncService.broadcastFamilyEvent(
                familyId,
                userId,
                "CATEGORY_RENAMED",
                "✏️ Renamed category \"" + oldName + "\" → \"" + category.getName() + "\""
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @PathVariable Integer categoryId
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyEdit(familyId, userId);

        FamilyCategory category = familyCategoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Category does not belong to this family");
        }

        String name = category.getName();
        familyCategoryRepo.delete(category);

        // 🔔 WS EVENT
        familySyncService.broadcastFamilyEvent(
                familyId,
                userId,
                "CATEGORY_DELETED",
                "🗑️ Deleted category \"" + name + "\""
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/spend")
    public ResponseEntity<?> spend(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @RequestBody FamilySpendRequest req
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyEdit(familyId, userId);

        FamilyExpense e = new FamilyExpense();
        e.setFamilyId(familyId);
        e.setCategoryId(req.getCategoryId());
        e.setUserId(userId);
        e.setAmount(req.getAmount());
        e.setNote(req.getNote());

        expenseRepo.save(e);
        familyBudgetService.spend(
                familyId,
                req.getMonth(),
                req.getYear(),
                req.getCategoryId(),
                req.getAmount(),
                userId
        );
        return ResponseEntity.ok().build();
    }
    @GetMapping("/expenses")
    public ResponseEntity<?> getExpenses(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId
    ) {
        Integer userId = userIdFromAuth(auth);
        familyAuth.authorizeFamilyView(familyId, userId);

        LocalDateTime from = LocalDateTime.now().minusDays(7);
        return ResponseEntity.ok(
            expenseRepo.findRecentExpenses(familyId, from)
        );
    }
    @GetMapping("/expenses/summary")
public ResponseEntity<?> monthlyExpenseSummary(
        @RequestHeader("Authorization") String auth,
        @PathVariable Integer familyId,
        @RequestParam int month,
        @RequestParam int year
) {
    Integer userId = userIdFromAuth(auth);
    familyAuth.authorizeFamilyView(familyId, userId);

    return ResponseEntity.ok(
        expenseRepo.getMonthlyExpenseSummary(familyId, month, year)
    );
}

}
