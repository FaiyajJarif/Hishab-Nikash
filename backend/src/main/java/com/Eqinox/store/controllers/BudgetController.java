package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.CategoryTargetRequest;
import com.Eqinox.store.dtos.DashboardSummaryDto;
import com.Eqinox.store.dtos.MoveMoneyRequest;
import com.Eqinox.store.dtos.budget.BudgetIncomeRequest;
import com.Eqinox.store.dtos.budget.BudgetPlanRequest;
import com.Eqinox.store.dtos.budget.BudgetTargetRequest;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.BudgetService;
import com.Eqinox.store.services.CategoryTargetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final AuthUserService authUserService;
    private final CategoryTargetService categoryTargetService;

    public BudgetController(
            BudgetService budgetService,
            AuthUserService authUserService,
            CategoryTargetService categoryTargetService
    ) {
        this.budgetService = budgetService;
        this.authUserService = authUserService;
        this.categoryTargetService = categoryTargetService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> summary(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        Integer userId = authUserService.getUserId(auth);
        DashboardSummaryDto dto = budgetService.getSummary(userId, month, year);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PostMapping("/income/set")
        public ResponseEntity<ApiResponse<Void>> setIncome(
                @RequestHeader("Authorization") String auth,
                @Valid @RequestBody BudgetIncomeRequest req
        ) {
            Integer userId = authUserService.getUserId(auth);
            BudgetPeriod period =
                budgetService.getOrCreateBudget(userId, req.getMonth(), req.getYear());

            budgetService.saveIncome(period, req.getIncome()); // overwrite
            return ResponseEntity.ok(ApiResponse.ok());
        }
        @PostMapping("/income/add")
        public ResponseEntity<ApiResponse<Void>> addIncome(
                @RequestHeader("Authorization") String auth,
                @Valid @RequestBody BudgetIncomeRequest req
        ) {
            Integer userId = authUserService.getUserId(auth);
            BudgetPeriod period =
                budgetService.getOrCreateBudget(userId, req.getMonth(), req.getYear());
        
            budgetService.addIncome(period, req.getIncome()); // add
            return ResponseEntity.ok(ApiResponse.ok());
        }        


    @PostMapping("/plan")
    public ResponseEntity<ApiResponse<Void>> plan(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody BudgetPlanRequest req
    ) {
        Integer userId = authUserService.getUserId(auth);
        BudgetPeriod period = budgetService.getOrCreateBudget(userId, req.getMonth(), req.getYear());

        // IMPORTANT: Use your real id getter (pick ONE that exists in BudgetPeriod)
        // If your entity uses getBudgetId() then keep it, otherwise change to getBudgetPeriodId() or getId()
        budgetService.planAmount(period.getBudgetId(), req.getCategoryId(), req.getAmount(), userId);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/move")
    public ResponseEntity<ApiResponse<Void>> moveMoney(
        @RequestHeader("Authorization") String auth,
        @RequestBody MoveMoneyRequest req
    ) {
        Integer userId = authUserService.getUserId(auth);

        BudgetPeriod period =
            budgetService.getOrCreateBudget(userId, req.month, req.year);

        budgetService.moveMoney(
            period,
            req.fromCategoryId,
            req.toCategoryId,
            req.amount
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/target")
public ResponseEntity<ApiResponse<Void>> target(
    @RequestHeader("Authorization") String auth,
    @Valid @RequestBody CategoryTargetRequest req
) {
    authUserService.getUserId(auth);
    categoryTargetService.saveTarget(req);
    return ResponseEntity.ok(ApiResponse.ok());
}

}
