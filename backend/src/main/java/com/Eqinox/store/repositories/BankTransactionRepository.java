package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.BankAccount;
import com.Eqinox.store.entities.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Integer> {

    List<BankTransaction> findByBankAccountIn(List<BankAccount> accounts);
}
