package com.Eqinox.store.config;

import com.Eqinox.store.entities.BankAccount;
import com.Eqinox.store.entities.BankProvider;
import com.Eqinox.store.repositories.BankAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class BankSeedConfig {

    @Bean
    CommandLineRunner seedBanks(BankAccountRepository repo) {
        return args -> {
            if (repo.count() == 0) {

                BankAccount b1 = new BankAccount();
                b1.setProvider(BankProvider.BKASH);
                b1.setAccountNumber("01711111111");
                b1.setOwnerName("Rakib Hasan");
                b1.setPin("1234");
                b1.setMockBalance(new BigDecimal("20000"));
                repo.save(b1);

                BankAccount b2 = new BankAccount();
                b2.setProvider(BankProvider.NAGAD);
                b2.setAccountNumber("01410039899");
                b2.setOwnerName("Eqinox User");
                b2.setPin("2222");
                b2.setMockBalance(new BigDecimal("10000"));
                repo.save(b2);

                BankAccount b3 = new BankAccount();
                b3.setProvider(BankProvider.DBBL);
                b3.setAccountNumber("ACC-998877");
                b3.setOwnerName("DBBL Nexus");
                b3.setPin("0000");
                b3.setMockBalance(new BigDecimal("50000"));
                repo.save(b3);
            }
        };
    }
}
