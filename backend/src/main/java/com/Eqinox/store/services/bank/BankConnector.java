package com.Eqinox.store.services.bank;

import com.Eqinox.store.entities.BankAccount;

import java.math.BigDecimal;

public interface BankConnector {

    BigDecimal fetchBalance(BankAccount account);

}
