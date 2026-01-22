package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.AllocateLeftoverRequest;
import com.Eqinox.store.dtos.CreateGoalRequest;
import com.Eqinox.store.entities.Goal;
import com.Eqinox.store.services.AnalyticsService;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final AuthUserService authUserService;
    private final GoalService goalService;
    private final AnalyticsService analyticsService;

    public GoalController(
            AuthUserService authUserService,
            GoalService goalService,
            AnalyticsService analyticsService
    ) {
        this.authUserService = authUserService;
        this.goalService = goalService;
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Goal>>> list(@RequestHeader("Authorization") String auth) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(goalService.getActiveGoals(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Goal>> create(
            @RequestHeader("Authorization") String auth,
            @RequestBody CreateGoalRequest req
    ) {
        Integer userId = authUserService.requireUserId(auth);
        Goal created = goalService.createGoal(userId, req.getName(), req.getTargetAmount(), req.getNote());
        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    @PostMapping("/allocate-leftover")
    public ResponseEntity<ApiResponse<Void>> allocateLeftover(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year,
            @RequestBody AllocateLeftoverRequest req
    ) {
        Integer userId = authUserService.requireUserId(auth);
        BigDecimal leftover = analyticsService.getMonthLeftover(userId, month, year);
        goalService.allocateLeftover(userId, month, year, leftover, req);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
