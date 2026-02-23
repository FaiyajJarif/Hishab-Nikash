package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.BankAccount;
import com.Eqinox.store.entities.BankProvider;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Integer> {
    Optional<BankAccount> findByProviderAndAccountNumber(BankProvider provider, String accountNumber);
}
