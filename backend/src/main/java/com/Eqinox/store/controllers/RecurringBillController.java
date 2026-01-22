package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.recurring.RecurringBillCreateRequest;
import com.Eqinox.store.dtos.recurring.RecurringBillDto;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.RecurringBillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-bills")
public class RecurringBillController {

    private final RecurringBillService recurringBillService;
    private final AuthUserService authUserService;

    public RecurringBillController(
            RecurringBillService recurringBillService,
            AuthUserService authUserService
    ) {
        this.recurringBillService = recurringBillService;
        this.authUserService = authUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecurringBillDto>>> list(
            @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = authUserService.requireUserId(authHeader);
        return ResponseEntity.ok(ApiResponse.ok(recurringBillService.list(userId)));
    }

    /**
     * Idempotency-Key is OPTIONAL but strongly recommended:
     * - If client retries the same request, server returns the same result (no duplicate bills).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RecurringBillDto>> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @Valid @RequestBody RecurringBillCreateRequest req
    ) {
        Integer userId = authUserService.requireUserId(authHeader);
        RecurringBillDto dto = recurringBillService.create(userId, req, idemKey);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggle(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @PathVariable Integer id
    ) {
        Integer userId = authUserService.requireUserId(authHeader);
        recurringBillService.toggle(userId, id, idemKey);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @PathVariable Integer id
    ) {
        Integer userId = authUserService.requireUserId(authHeader);
        recurringBillService.delete(userId, id, idemKey);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
