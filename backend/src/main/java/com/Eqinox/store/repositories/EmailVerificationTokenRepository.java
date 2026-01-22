package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {

    Optional<EmailVerificationToken> findByToken(String token);

    // for cleanup or checking existing tokens
    List<EmailVerificationToken> findByUserId(Integer userId);
}
