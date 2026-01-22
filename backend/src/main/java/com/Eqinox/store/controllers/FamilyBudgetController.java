package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.CreateFamilyCategoryRequest;
import com.Eqinox.store.dtos.FamilyIncomeRequest;
import com.Eqinox.store.dtos.FamilyPlanRequest;
import com.Eqinox.store.entities.FamilyBudgetPeriod;
import com.Eqinox.store.entities.FamilyCategory;
import com.Eqinox.store.repositories.FamilyCategoryRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.FamilyAuthorizationService;
import com.Eqinox.store.services.FamilyBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/family/{familyId}/budget")
public class FamilyBudgetController {

    private final JwtService jwtService;
    private final FamilyAuthorizationService familyAuth;
    private final FamilyBudgetService familyBudgetService;
    private final FamilyCategoryRepository familyCategoryRepo;

    public FamilyBudgetController(
            JwtService jwtService,
            FamilyAuthorizationService familyAuth,
            FamilyBudgetService familyBudgetService,
            FamilyCategoryRepository familyCategoryRepo
    ) {
        this.jwtService = jwtService;
        this.familyAuth = familyAuth;
        this.familyBudgetService = familyBudgetService;
        this.familyCategoryRepo = familyCategoryRepo;
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

        return ResponseEntity.ok(res);
    }
}
