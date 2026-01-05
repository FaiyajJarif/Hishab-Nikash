package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.UserDto;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.mappers.UserMapper;
import com.Eqinox.store.repositories.OnboardingSelectionRepository;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.repositories.OnboardingSelectionRepository;


import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final OnboardingSelectionRepository onboardingSelectionRepository;

    public UserApiController(
            UserRepository userRepository,
            UserMapper userMapper,
            JwtService jwtService,
            OnboardingSelectionRepository onboardingSelectionRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.onboardingSelectionRepository = onboardingSelectionRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtService.getEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/dashboard-name")
    public ResponseEntity<?> getDashboardName(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.getEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return onboardingSelectionRepository
                .findTopByUserIdAndStepAndCategory(
                        user.getUserId(),
                        "STEP_0",
                        "display_name")
                .map(selection -> ResponseEntity.ok(
                        Map.of("name", selection.getValue())))
                .orElse(
                        ResponseEntity.ok(
                                Map.of("name", user.getName())));
    }

}
