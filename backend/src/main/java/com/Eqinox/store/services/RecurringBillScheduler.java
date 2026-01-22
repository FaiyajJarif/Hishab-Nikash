package com.Eqinox.store.services;

import com.Eqinox.store.entities.RecurringBill;
import com.Eqinox.store.repositories.RecurringBillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecurringBillScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecurringBillScheduler.class);

    private final RecurringBillRepository billRepo;
    private final RecurringBillProcessor processor;

    public RecurringBillScheduler(
            RecurringBillRepository billRepo,
            RecurringBillProcessor processor
    ) {
        this.billRepo = billRepo;
        this.processor = processor;
        log.info("✅ RecurringBillScheduler initialized");
    }

    // Keep your mock 5 sec schedule (as you requested)
    @Scheduled(cron = "*/5 * * * * *", zone = "Asia/Dhaka")
    public void runDailyBills() {
        LocalDate today = LocalDate.now();

        List<RecurringBill> dueBills = billRepo.findDueBills(today);

        for (RecurringBill bill : dueBills) {
            processor.processBill(bill.getId(), today);
        }
    }
}
