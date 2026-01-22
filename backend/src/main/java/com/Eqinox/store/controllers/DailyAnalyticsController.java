package com.Eqinox.store.controllers;

import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.DailyAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class DailyAnalyticsController {

    private final DailyAnalyticsService analyticsService;
    private final JwtService jwtService;

    public DailyAnalyticsController(
            DailyAnalyticsService analyticsService,
            JwtService jwtService) {
        this.analyticsService = analyticsService;
        this.jwtService = jwtService;
    }

    private Integer userId(String auth) {
        return jwtService.extractUserId(auth.substring(7));
    }

    // 📅 Daily total
    @GetMapping("/day/summary")
    public BigDecimal dailySummary(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date) {

        return analyticsService.getDailySpend(
                userId(auth),
                LocalDate.parse(date)
        );
    }

    // 🗓 Monthly calendar
    @GetMapping("/month/calendar")
    public Map<LocalDate, BigDecimal> calendar(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year) {

        return analyticsService.getMonthlyCalendar(
                userId(auth),
                month,
                year
        );
    }

    // 🧾 Daily category breakdown
    @GetMapping("/day/categories")
    public Map<String, BigDecimal> dailyCategories(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date) {

        return analyticsService.getDailyCategories(
                userId(auth),
                LocalDate.parse(date)
        );
    }
}
