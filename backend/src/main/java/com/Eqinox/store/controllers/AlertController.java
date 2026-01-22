package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.entities.Alert;
import com.Eqinox.store.repositories.AlertRepository;
import com.Eqinox.store.services.AuthUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alertRepo;
    private final AuthUserService authUserService;

    public AlertController(AlertRepository alertRepo, AuthUserService authUserService) {
        this.alertRepo = alertRepo;
        this.authUserService = authUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Alert>>> getAlerts(
            @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = authUserService.requireUserId(authHeader);
        List<Alert> alerts = alertRepo.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(ApiResponse.ok(alerts));
    }
}
