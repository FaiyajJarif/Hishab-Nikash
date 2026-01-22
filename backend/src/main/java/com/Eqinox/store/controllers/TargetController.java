package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.entities.Target;
import com.Eqinox.store.repositories.TargetRepository;
import com.Eqinox.store.services.AuthUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/targets")
public class TargetController {

    private final TargetRepository targetRepo;
    private final AuthUserService authUserService;

    public TargetController(TargetRepository targetRepo, AuthUserService authUserService) {
        this.targetRepo = targetRepo;
        this.authUserService = authUserService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Target>> saveTarget(
            @RequestHeader("Authorization") String auth,
            @RequestBody Target target
    ) {
        Integer userId = authUserService.requireUserId(auth);
        target.setUserId(userId);
        Target saved = targetRepo.save(target);
        return ResponseEntity.ok(ApiResponse.ok(saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Target>>> getTargets(
            @RequestHeader("Authorization") String auth
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(targetRepo.findByUserId(userId)));
    }
}
