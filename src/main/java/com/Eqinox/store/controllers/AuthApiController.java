package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.*;
import com.Eqinox.store.entities.EmailVerificationToken;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.EmailVerificationTokenRepository;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.JwtService;
import com.Eqinox.store.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    // ---------- LOGIN (REST + JWT) ----------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();
        boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid email or password", null));
        }

        User user = userOpt.get();

        if (user.getPasswordHash() == null ||
                !BCrypt.checkpw(password, user.getPasswordHash())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid email or password", null));
        }

        if (Boolean.FALSE.equals(user.getIsVerified())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Please verify your email before logging in.", null));
        }

        // ✅ Generate JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getRole(),
                rememberMe
        );

        return ResponseEntity.ok(new AuthResponse(true, "Login successful", token));
    }

    // ---------- SIGNUP (REST) ----------
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest req) {

        String name = req.getName();
        String email = req.getEmail();
        String password = req.getPassword();
        String confirmPassword = req.getConfirmPassword();
        String dateOfBirth = req.getDateOfBirth();
        String timezone = req.getTimezone();
        Integer budgetStartDay = req.getBudgetStartDay();

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Name, email and password are required"));
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Passwords do not match"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Email already registered"));
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);

        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            user.setDateOfBirth(LocalDate.parse(dateOfBirth));
        }

        if (timezone != null && !timezone.isBlank()) {
            user.setTimezone(timezone);
        }

        if (budgetStartDay == null || budgetStartDay < 1 || budgetStartDay > 28) {
            user.setBudgetStartDay(1);
        } else {
            user.setBudgetStartDay(budgetStartDay);
        }

        user.setRole("NORMAL_USER");
        user.setSubscriptionId(1); // free trial
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setIsVerified(false);

        User savedUser = userRepository.save(user);

        // create token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUserId(savedUser.getUserId());
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(OffsetDateTime.now().plusDays(1));
        verificationToken.setUsed(false);
        tokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/verify-email?token=" + token;
        System.out.println("Verification link: " + verificationLink);

        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);
            return ResponseEntity.ok(ApiResponse.success("Account created. Check your email to verify."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.success(
                    "Account created, but we could not send verification email. Use RESEND from login page."
            ));
        }
    }

    // ---------- LOGOUT (MVP) ----------
    // With JWT, logout on MVP = frontend deletes token.
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out (client should delete token)."));
    }

    // ---------- RESEND VERIFICATION ----------
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(@RequestBody ResendVerificationRequest req) {

        String email = req.getEmail();
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Email is required"));
        }

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("No account found with this email"));
        }

        var user = userOpt.get();

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("This email is already verified. You can log in."));
        }

        var tokens = tokenRepository.findByUserId(user.getUserId());
        for (var t : tokens) t.setUsed(true);
        tokenRepository.saveAll(tokens);

        String token = UUID.randomUUID().toString();
        var verificationToken = new EmailVerificationToken();
        verificationToken.setUserId(user.getUserId());
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(OffsetDateTime.now().plusDays(1));
        verificationToken.setUsed(false);
        tokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/verify-email?token=" + token;

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("Could not send verification email. Try again later."));
        }

        return ResponseEntity.ok(ApiResponse.success("Verification email sent again. Please check your inbox."));
    }
}
