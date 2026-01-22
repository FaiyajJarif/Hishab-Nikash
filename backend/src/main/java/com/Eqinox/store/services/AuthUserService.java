package com.Eqinox.store.services;

import com.Eqinox.store.api.UnauthorizedException;
import com.Eqinox.store.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    private final JwtService jwtService;

    public AuthUserService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Integer requireUserId(String authHeader) {
        String token = extractBearerToken(authHeader);
        Integer userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new UnauthorizedException("Invalid token (userId missing)");
        }
        return userId;
    }

    public String requireEmail(String authHeader) {
        String token = extractBearerToken(authHeader);
        String email = jwtService.getEmail(token);
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Invalid token (email missing)");
        }
        return email;
    }

    public Integer getUserId(String authHeader) {
        return requireUserId(authHeader);
    }

    private String extractBearerToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new UnauthorizedException("Missing Authorization header");
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header must start with Bearer");
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("Bearer token is empty");
        }
        return token;
    }
}
