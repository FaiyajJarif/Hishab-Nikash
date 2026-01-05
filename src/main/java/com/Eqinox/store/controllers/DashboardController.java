package com.Eqinox.store.controllers;

import com.Eqinox.store.entities.Category;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.CategoryGoalRepository;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.CategoryAvailableService;
import com.Eqinox.store.services.CategoryInitializationService;
import com.Eqinox.store.services.CategoryProgressService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final JwtService jwtService;
    private final CategoryInitializationService categoryInitializationService;
    private final CategoryGoalRepository categoryGoalRepository;
    private final CategoryProgressService categoryProgressService;
    private final CategoryAvailableService availableService;

    public DashboardController(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            JwtService jwtService,
            CategoryInitializationService categoryInitializationService,
            CategoryGoalRepository categoryGoalRepository,
            CategoryProgressService categoryProgressService,
            CategoryAvailableService availableService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.jwtService = jwtService;
        this.categoryInitializationService = categoryInitializationService;
        this.categoryGoalRepository = categoryGoalRepository;
        this.categoryProgressService = categoryProgressService;
        this.availableService = availableService;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int month,
            @RequestParam int year) {

        String email = jwtService.getEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email).orElseThrow();

        if (categoryRepository.findByUserId(user.getUserId()).isEmpty()) {
            categoryInitializationService.initializeUserCategories(user);
        }

        List<Category> categories = categoryRepository.findByUserId(user.getUserId());

        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();

        for (Category c : categories) {

            var goal = categoryGoalRepository
                    .findByCategoryIdAndMonthAndYear(
                            c.getCategoryId(), month, year)
                    .orElse(null);

            Map<String, Object> row = new HashMap<>();
            row.put("id", c.getCategoryId());
            row.put("name", c.getName());
            row.put("target", goal == null ? 0 : goal.getTargetAmount());
            row.put("frequency", goal == null ? "" : goal.getFrequency());

            result.computeIfAbsent(c.getType(), k -> new ArrayList<>())
                    .add(row);
            BigDecimal available = availableService.calculateAvailable(
                    c.getCategoryId(),
                    month,
                    year);

            row.put("available", available);
        }

        return ResponseEntity.ok(result);
    }
}
