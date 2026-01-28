package com.Eqinox.store.services;

import com.Eqinox.store.dtos.BillNotificationDto;
import com.Eqinox.store.entities.Alert;
import com.Eqinox.store.entities.BillFrequency;
import com.Eqinox.store.entities.RecurringBill;
import com.Eqinox.store.entities.RecurringBillRun;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.AlertRepository;
import com.Eqinox.store.repositories.RecurringBillRepository;
import com.Eqinox.store.repositories.RecurringBillRunRepository;
import com.Eqinox.store.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecurringBillProcessor {

    private final RecurringBillRepository billRepo;
    private final RecurringBillRunRepository runRepo;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepo;
    private final AlertRepository alertRepository;
    private final FamilySyncService familySyncService;

    public RecurringBillProcessor(
            RecurringBillRepository billRepo,
            RecurringBillRunRepository runRepo,
            TransactionService transactionService,
            NotificationService notificationService,
            EmailService emailService,
            UserRepository userRepo,
            AlertRepository alertRepository,
            FamilySyncService familySyncService
    ) {
        this.billRepo = billRepo;
        this.runRepo = runRepo;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.alertRepository = alertRepository;
        this.familySyncService = familySyncService;
    }

    /**
     * ✅ Idempotent processing:
     * - Locks the bill row
     * - Inserts (billId, dueDate) into recurring_bill_run
     * - If duplicate -> skip
     */
    @Async("billExecutor")
    @Transactional
    public void processBill(Integer billId, LocalDate today) {

        RecurringBill bill = billRepo.findByIdForUpdate(billId).orElse(null);
        if (bill == null) return;

        if (!Boolean.TRUE.equals(bill.isActive())) return;

        LocalDate dueDate = bill.getNextDueDate();
        if (dueDate == null) return;

        // Not due yet? skip.
        if (dueDate.isAfter(today)) return;

        // ✅ processed-once guard (DB unique constraint on bill_id + due_date)
        try {
            RecurringBillRun run = new RecurringBillRun();
            run.setBillId(bill.getId());
            run.setDueDate(dueDate);
            runRepo.save(run);
        } catch (DataIntegrityViolationException dup) {
            return; // already processed this bill for this dueDate
        }

        // 1) Create expense transaction
        try {
            transactionService.createExpenseFromRecurringBill(bill);
        } catch (Exception e) {
            log.error("Failed to process recurring bill {}", bill.getId(), e);
            // DO NOT rethrow
        }

        // 2) Add alert row
        alertRepository.save(new Alert(
                bill.getUserId(),
                "💸 Bill paid: " + bill.getName() + " (৳" + bill.getAmount() + ")"
        ));

        // 3) WS payload
        BillNotificationDto payload = new BillNotificationDto(
                bill.getId(),
                bill.getName(),
                bill.getAmount(),
                today,
                "Recurring bill paid automatically"
        );

        // 4) WS notify
        notificationService.notifyUser(bill.getUserId(), payload);

        // 5) Email notify
        User user = userRepo.findById(bill.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + bill.getUserId()));

        emailService.sendBillEmail(
                user.getEmail(),
                bill.getName(),
                bill.getAmount().toString(),
                today.toString()
        );

        // 6) Update next due date (use dueDate to avoid drift)
        bill.setNextDueDate(nextDate(dueDate, bill.getFrequency()));
        billRepo.save(bill);

        // Optional: family broadcast
        familySyncService.broadcastUserChange(
                bill.getUserId(),
                "BILL_PAID",
                "💸 Recurring bill paid: " + bill.getName()
        );
    }

    private LocalDate nextDate(LocalDate from, BillFrequency f) {
        return switch (f) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case MONTHLY -> from.plusMonths(1);
        };
    }
}
