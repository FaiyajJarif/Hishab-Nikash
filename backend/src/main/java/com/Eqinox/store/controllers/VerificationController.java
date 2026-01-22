package com.Eqinox.store.controllers;

import com.Eqinox.store.entities.EmailVerificationToken;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.EmailVerificationTokenRepository;
import com.Eqinox.store.repositories.UserRepository;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.util.Optional;

@Controller
public class VerificationController {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/verify-email")
    public void verifyEmail(
            @RequestParam String token,
            HttpServletResponse response) throws IOException {

        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            response.sendRedirect("http://localhost:5173/verify-email?status=invalid");
            return;
        }

        EmailVerificationToken vToken = tokenOpt.get();

        if (vToken.isUsed() || vToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            response.sendRedirect("http://localhost:5173/verify-email?status=expired");
            return;
        }

        User user = userRepository.findById(vToken.getUserId()).orElse(null);
        if (user == null) {
            response.sendRedirect("http://localhost:5173/verify-email?status=invalid");
            return;
        }

        user.setIsVerified(true);
        userRepository.save(user);

        vToken.setUsed(true);
        tokenRepository.save(vToken);

        // ✅ SUCCESS
        response.sendRedirect("http://localhost:5173/verify-email?status=success");
    }

}