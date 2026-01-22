package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.MonthCloseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class MonthCloseController {

    private final MonthCloseService monthCloseService;
    private final AuthUserService authUserService;

    public MonthCloseController(
            MonthCloseService monthCloseService,
            AuthUserService authUserService
    ) {
        this.monthCloseService = monthCloseService;
        this.authUserService = authUserService;
    }

    @PostMapping("/close-month")
    public ResponseEntity<ApiResponse<Void>> closeMonth(
            @RequestHeader("Authorization") String auth,
            @RequestParam int month,
            @RequestParam int year
    ) {
        Integer userId = authUserService.requireUserId(auth);
        monthCloseService.closeMonth(userId, month, year);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
