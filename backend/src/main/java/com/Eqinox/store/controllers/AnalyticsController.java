package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.analytics.*;
import com.Eqinox.store.entities.MonthlyAnalyticsSnapshot;
import com.Eqinox.store.services.AnalyticsService;
import com.Eqinox.store.services.AuthUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AuthUserService authUserService;
    private final AnalyticsService analyticsService;

    public AnalyticsController(AuthUserService authUserService, AnalyticsService analyticsService) {
        this.authUserService = authUserService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailySummaryDto>> daily(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date
    ) {
        Integer userId = authUserService.requireUserId(auth);
        DailySummaryDto dto = analyticsService.getDailySummary(userId, LocalDate.parse(date));
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/daily/categories")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownDto>>> dailyCategories(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getDailyExpenseByCategory(userId, LocalDate.parse(date))
        ));
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<CalendarDayDto>>> calendar(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getCalendarHeatmap(userId, month, year)
        ));
    }

    @GetMapping("/month/summary")
    public ResponseEntity<ApiResponse<MonthSummaryDto>> monthSummary(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getMonthSummary(userId, month, year)
        ));
    }

    @GetMapping("/month/categories")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownDto>>> monthCategories(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getMonthExpenseByCategory(userId, month, year)
        ));
    }

    @GetMapping("/month/trend")
    public ResponseEntity<ApiResponse<MonthTrendDto>> monthTrend(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getMonthWeeklyTrend(userId, month, year)
        ));
    }

    @PostMapping("/month/close")
    public ResponseEntity<ApiResponse<MonthlyAnalyticsSnapshot>> closeMonth(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.closeMonth(userId, month, year)
        ));
    }

    @PostMapping("/month/reopen")
        public ResponseEntity<ApiResponse<Void>> reopenMonth(
                @RequestHeader("Authorization") String auth,
                @RequestParam int month,
                @RequestParam int year
        ) {
        Integer userId = authUserService.requireUserId(auth);
        analyticsService.reopenMonth(userId, month, year);
        return ResponseEntity.ok(ApiResponse.ok(null));
        }

    @GetMapping("/rolling")
    public ResponseEntity<ApiResponse<List<RollingAverageDto>>> rollingAverage(
            @RequestHeader("Authorization") String auth,
            @RequestParam int days,
            @RequestParam String endDate
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getRollingDailyExpense(userId, days, LocalDate.parse(endDate))
        ));
    }

    @GetMapping("/alerts/daily")
    public ResponseEntity<ApiResponse<DailyOverspendAlertDto>> dailyOverspendAlert(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getDailyOverspendAlert(userId, LocalDate.parse(date))
        ));
    }

    @GetMapping("/alerts/category")
    public ResponseEntity<ApiResponse<List<CategoryOverspendDto>>> categoryOverspend(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getCategoryOverspend(userId, month, year)
        ));
    }

    @GetMapping("/insights/daily")
    public ResponseEntity<ApiResponse<InsightDto>> dailyInsight(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getDailyInsight(userId, LocalDate.parse(date))
        ));
    }

    @GetMapping("/insights/category")
    public ResponseEntity<ApiResponse<InsightDto>> categoryInsight(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer categoryId,
            @RequestParam String date
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                analyticsService.getCategoryInsight(userId, categoryId, LocalDate.parse(date))
        ));
    }
    @GetMapping("/month/history")
        public ResponseEntity<ApiResponse<List<MonthlyAnalyticsSnapshot>>> history(
                @RequestHeader("Authorization") String auth
        ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(
                ApiResponse.ok(analyticsService.getMonthHistory(userId))
        );
        }
}
