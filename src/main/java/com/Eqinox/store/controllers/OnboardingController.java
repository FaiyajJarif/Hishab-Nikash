package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.OnboardingStepRequest;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.repositories.OnboardingSelectionRepository;
import com.Eqinox.store.entities.UserOnboardingSelection;
import com.Eqinox.store.services.CategoryInitializationService;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final UserRepository userRepository;
    private final OnboardingSelectionRepository selectionRepository;
    private final CategoryInitializationService categoryInitializationService;

    public OnboardingController(
            UserRepository userRepository,
            OnboardingSelectionRepository selectionRepository,
            CategoryInitializationService categoryInitializationService) {

        this.userRepository = userRepository;
        this.selectionRepository = selectionRepository;
        this.categoryInitializationService = categoryInitializationService;
    }

    @Autowired
    private JwtService jwtService;

    @PostMapping("/step")
    public ResponseEntity<?> saveStep(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody OnboardingStepRequest request) {

        String token = authHeader.substring(7);
        String email = jwtService.getEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // delete previous step data
        selectionRepository.deleteByUserIdAndStep(
                user.getUserId(),
                request.getStep());

        List<UserOnboardingSelection> entities = request.getSelections().stream()
                .map(s -> new UserOnboardingSelection(
                        user.getUserId(),
                        request.getStep(),
                        s.getCategory(),
                        s.getValue(),
                        s.getFrequency()))
                .toList();

        selectionRepository.saveAll(entities);

        // ✅ mark onboarding complete on LAST STEP
        if ("STEP_3".equals(request.getStep())) {
            user.setOnboardingCompleted(true);
            userRepository.save(user);

            // 🔥 create categories now
            categoryInitializationService.initializeUserCategories(user);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<?> getOnboardingStatus(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.getEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        // If already completed → go to dashboard
        if (Boolean.TRUE.equals(user.getOnboardingCompleted())) {
            return ResponseEntity.ok(
                    Map.of("completed", true));
        }

        return selectionRepository
                .findTopByUserIdOrderByIdDesc(user.getUserId())
                .map(selection -> ResponseEntity.ok(
                        Map.of(
                                "completed", false,
                                "lastStep", selection.getStep())))
                .orElse(ResponseEntity.ok(
                        Map.of(
                                "completed", false,
                                "lastStep", "STEP_0")));
    }

}
