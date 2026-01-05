package com.Eqinox.store.controllers;

import com.Eqinox.store.entities.EmailVerificationToken;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.EmailVerificationTokenRepository;
import com.Eqinox.store.repositories.UserRepository;
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
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Invalid or expired verification link.");
            return "loginPage";
        }

        EmailVerificationToken verificationToken = tokenOpt.get();

        if (verificationToken.isUsed() ||
                verificationToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            model.addAttribute("error", "Verification link is no longer valid.");
            return "loginPage";
        }

        // Mark user as verified
        User user = userRepository.findById(verificationToken.getUserId())
                .orElse(null);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "loginPage";
        }

        user.setIsVerified(true);
        userRepository.save(user);

        // Mark token as used
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        model.addAttribute("message", "Email verified successfully. You can now log in.");
        return "loginPage";
    }
}