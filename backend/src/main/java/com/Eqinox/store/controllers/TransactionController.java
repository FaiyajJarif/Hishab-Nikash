package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.CreateTransactionRequest;
import com.Eqinox.store.dtos.UpdateTransactionRequest;
import com.Eqinox.store.entities.Transaction;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthUserService authUserService;

    public TransactionController(
            TransactionService transactionService,
            AuthUserService authUserService
    ) {
        this.transactionService = transactionService;
        this.authUserService = authUserService;
    }

    // ✅ ADD EXPENSE
    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<Void>> addExpense(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody CreateTransactionRequest req
    ) {
        Integer userId = authUserService.getUserId(auth);

        transactionService.createExpense(
                userId,
                req.getCategoryId(),
                req.getAmount(),
                req.getDate(),
                req.getNote()
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

    // ✅ EDIT TRANSACTION (correctness lock)
    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer transactionId,
            @Valid @RequestBody UpdateTransactionRequest req
    ) {
        Integer userId = authUserService.getUserId(auth);

        Transaction updated = transactionService.updateTransaction(
                userId,
                transactionId,
                req
        );

        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    // ✅ DELETE TRANSACTION (correctness lock)
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer transactionId
    ) {
        Integer userId = authUserService.getUserId(auth);

        transactionService.deleteTransaction(userId, transactionId);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    // ✅ RECENT TRANSACTIONS (for dashboard panel)
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<Transaction>>> recent(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Integer userId = authUserService.getUserId(auth);
        List<Transaction> list = transactionService.getRecentTransactions(userId, limit);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
