package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.CategorySpendingAnomalyDto;
import com.Eqinox.store.dtos.analytics.CategoryAnomalyDto;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.CategoryAnomalyService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics/anomalies")
public class CategoryAnomalyController {

    private final CategoryAnomalyService service;
    private final JwtService jwtService;

    public CategoryAnomalyController(
            CategoryAnomalyService service,
            JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    private Integer userId(String auth) {
        return jwtService.extractUserId(auth.substring(7));
    }

    @GetMapping("/category")
    public List<CategorySpendingAnomalyDto> categoryAnomaly(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date) {

        return service.detect(
                userId(auth),
                LocalDate.parse(date)
        );
    }

    @GetMapping("/daily")
    public List<CategoryAnomalyDto> dailyAnomalies(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date) {

        Integer userId = jwtService.extractUserId(auth.substring(7));
        return service.detectDailyAnomalies(userId, LocalDate.parse(date));
    }
}
