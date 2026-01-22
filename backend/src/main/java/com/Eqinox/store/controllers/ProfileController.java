package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.DashboardSummaryDto;
import com.Eqinox.store.dtos.UserDto;
import com.Eqinox.store.dtos.analytics.MonthSummaryDto;
import com.Eqinox.store.dtos.profile.ProfileOverviewDto;
import com.Eqinox.store.entities.Alert;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.mappers.UserMapper;
import com.Eqinox.store.repositories.AlertRepository;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.services.AnalyticsService;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AuthUserService authUserService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;
    private final AlertRepository alertRepository;

    public ProfileController(
            AuthUserService authUserService,
            UserRepository userRepository,
            UserMapper userMapper,
            BudgetService budgetService,
            AnalyticsService analyticsService,
            AlertRepository alertRepository
    ) {
        this.authUserService = authUserService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.budgetService = budgetService;
        this.analyticsService = analyticsService;
        this.alertRepository = alertRepository;
    }

    /**
     * One endpoint that returns:
     * - user profile basics
     * - budget summary (income/assigned/remaining)
     * - monthly analytics summary
     * - latest alerts
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<ProfileOverviewDto>> overview(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);

        User user = userRepository.findById(userId).orElseThrow();
        UserDto userDto = userMapper.toDto(user);

        DashboardSummaryDto budgetSummary = budgetService.getSummary(userId, month, year);
        MonthSummaryDto monthAnalytics = analyticsService.getMonthSummary(userId, month, year);
        List<Alert> alerts = alertRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);

        ProfileOverviewDto dto = new ProfileOverviewDto(userDto, budgetSummary, monthAnalytics, alerts);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
