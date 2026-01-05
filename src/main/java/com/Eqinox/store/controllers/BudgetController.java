package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.CategoryTargetRequest;
import com.Eqinox.store.dtos.PlanBudgetRequest;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.BudgetService;
import com.Eqinox.store.services.CategoryTargetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryTargetService categoryTargetService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public BudgetController(
            BudgetService budgetService,
            CategoryTargetService categoryTargetService,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.budgetService = budgetService;
        this.categoryTargetService = categoryTargetService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // ✅ plan monthly budget
    @PostMapping("/plan")
    public ResponseEntity<?> planBudget(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PlanBudgetRequest request) {

        String email = jwtService.getEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email).orElseThrow();

        BudgetPeriod period =
                budgetService.getOrCreateCurrentBudget(user.getUserId());

        budgetService.syncBudgetItems(period);
        budgetService.planAmount(
                period.getBudgetId(),
                request.getCategoryId(),
                request.getAmount()
        );

        return ResponseEntity.ok().build();
    }

    // ✅ YNAB-style target (PER CATEGORY, PER MONTH)
    @PostMapping("/target")
    public ResponseEntity<?> setCategoryTarget(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CategoryTargetRequest req) {

        String email = jwtService.getEmail(authHeader.substring(7));
        userRepository.findByEmail(email).orElseThrow();

        categoryTargetService.saveTarget(
                req.getCategoryId(),
                req.getMonth(),
                req.getYear(),
                req.getAmount(),
                req.getFrequency()
        );

        return ResponseEntity.ok().build();
    }
}
