package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.UserBankLink;
import com.Eqinox.store.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBankLinkRepository
        extends JpaRepository<UserBankLink, Integer> {

    // Get all bank links for a user
    List<UserBankLink> findByUserId(Integer userId);

    // Check if user already linked this bank account
    boolean existsByUserIdAndBankAccount(Integer userId, BankAccount bankAccount);
}
