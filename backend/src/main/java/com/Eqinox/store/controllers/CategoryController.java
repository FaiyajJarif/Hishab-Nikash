package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.CreateCategoryRequest;
import com.Eqinox.store.entities.Category;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.services.AuthUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final AuthUserService authUserService;

    public CategoryController(
            CategoryRepository categoryRepository,
            AuthUserService authUserService
    ) {
        this.categoryRepository = categoryRepository;
        this.authUserService = authUserService;
    }

    // ✅ CREATE CATEGORY (scoped to logged-in user)
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateCategoryRequest req
    ) {
        Integer userId = authUserService.getUserId(authHeader);

        Category c = new Category();
        c.setUserId(userId);
        c.setName(req.getName());
        c.setType(req.getType());

        Category saved = categoryRepository.save(c);
        return ResponseEntity.ok(ApiResponse.ok(saved));
    }

    // ✅ TOGGLE ROLLOVER (scoped)
    @PutMapping("/{categoryId}/rollover")
    public ResponseEntity<ApiResponse<Void>> toggleRollover(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer categoryId,
            @RequestParam boolean enabled
    ) {
        Integer userId = authUserService.getUserId(authHeader);

        Category category = categoryRepository
                .findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setRolloverEnabled(enabled);
        categoryRepository.save(category);

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
