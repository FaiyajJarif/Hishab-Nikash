package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.UserDto;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.mappers.UserMapper;
import com.Eqinox.store.repositories.OnboardingSelectionRepository;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.services.AuthUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthUserService authUserService;
    private final OnboardingSelectionRepository onboardingSelectionRepository;

    public UserApiController(
            UserRepository userRepository,
            UserMapper userMapper,
            AuthUserService authUserService,
            OnboardingSelectionRepository onboardingSelectionRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authUserService = authUserService;
        this.onboardingSelectionRepository = onboardingSelectionRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me(
            @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = authUserService.requireUserId(authHeader);
        User user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(userMapper.toDto(user)));
    }

    @GetMapping("/dashboard-name")
    public ResponseEntity<ApiResponse<Map<String, String>>> getDashboardName(
            @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = authUserService.requireUserId(authHeader);

        User user = userRepository.findById(userId).orElseThrow();

        String name = onboardingSelectionRepository
                .findTopByUserIdAndStepAndCategory(userId, "STEP_0", "display_name")
                .map(selection -> selection.getValue())
                .orElse(user.getName());

        return ResponseEntity.ok(ApiResponse.ok(Map.of("name", name)));
    }
}
