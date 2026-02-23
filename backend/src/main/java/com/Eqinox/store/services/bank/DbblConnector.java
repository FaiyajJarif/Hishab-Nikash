package com.Eqinox.store.services.bank;

import com.Eqinox.store.entities.BankAccount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DbblConnector implements BankConnector {

    @Override
    public BigDecimal fetchBalance(BankAccount account) {
        simulateDelay();
        return account.getMockBalance();
    }

    private void simulateDelay() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }
}
