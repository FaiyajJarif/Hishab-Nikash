package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.SpendingAnomalyDto;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.SpendingAnomalyService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics/anomaly")
public class SpendingAnomalyController {

    private final SpendingAnomalyService anomalyService;
    private final JwtService jwtService;

    public SpendingAnomalyController(
            SpendingAnomalyService anomalyService,
            JwtService jwtService) {
        this.anomalyService = anomalyService;
        this.jwtService = jwtService;
    }

    private Integer userId(String auth) {
        return jwtService.extractUserId(auth.substring(7));
    }

    @GetMapping("/day")
    public SpendingAnomalyDto dailyAnomaly(
            @RequestHeader("Authorization") String auth,
            @RequestParam String date) {

        return anomalyService.checkDailyAnomaly(
                userId(auth),
                LocalDate.parse(date)
        );
    }
}
