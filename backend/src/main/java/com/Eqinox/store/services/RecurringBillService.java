package com.Eqinox.store.services;

import com.Eqinox.store.dtos.BillNotificationDto;
import com.Eqinox.store.dtos.recurring.RecurringBillCreateRequest;
import com.Eqinox.store.dtos.recurring.RecurringBillDto;
import com.Eqinox.store.entities.RecurringBill;
import com.Eqinox.store.entities.RecurringBillIdempotency;
import com.Eqinox.store.repositories.RecurringBillIdempotencyRepository;
import com.Eqinox.store.repositories.RecurringBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecurringBillService {

    private final RecurringBillRepository billRepo;
    private final RecurringBillIdempotencyRepository idemRepo;
    private final NotificationService notificationService;

    public RecurringBillService(
            RecurringBillRepository billRepo,
            RecurringBillIdempotencyRepository idemRepo,
            NotificationService notificationService
    ) {
        this.billRepo = billRepo;
        this.idemRepo = idemRepo;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<RecurringBillDto> list(Integer userId) {
        return billRepo.findByUserId(userId).stream()
                .map(RecurringBillDto::from)
                .toList();
    }

    @Transactional
    public RecurringBillDto create(Integer userId, RecurringBillCreateRequest req, String idemKey) {
        // ✅ Client-retry protection
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = idemRepo.findByUserIdAndIdemKeyAndOperation(userId, idemKey, "CREATE");
            if (existing.isPresent()) {
                Integer billId = existing.get().getBillId();
                RecurringBill bill = billRepo.findByIdAndUserId(billId, userId)
                        .orElseThrow(() -> new IllegalStateException("Idempotency record exists but bill missing"));
                return RecurringBillDto.from(bill);
            }
        }

        RecurringBill bill = new RecurringBill();
        bill.setUserId(userId);
        bill.setCategoryId(req.getCategoryId());
        bill.setName(req.getName());
        bill.setAmount(req.getAmount());
        bill.setFrequency(req.getFrequency());
        bill.setNextDueDate(req.getNextDueDate());
        bill.setActive(true);

        RecurringBill saved = billRepo.save(bill);

        if (idemKey != null && !idemKey.isBlank()) {
            RecurringBillIdempotency rec = RecurringBillIdempotency.create(userId, idemKey, "CREATE", saved.getId());
            idemRepo.save(rec);
        }

        // Optional: notify that a bill was created (your UI used this earlier)
        notificationService.notifyUser(
                userId,
                new BillNotificationDto(
                        saved.getId(),
                        saved.getName(),
                        saved.getAmount(),
                        saved.getNextDueDate(),
                        "📌 New recurring bill added"
                )
        );

        return RecurringBillDto.from(saved);
    }

    @Transactional
    public void toggle(Integer userId, Integer billId, String idemKey) {
        if (idemKey != null && !idemKey.isBlank()) {
            if (idemRepo.existsByUserIdAndIdemKeyAndOperation(userId, idemKey, "TOGGLE")) {
                return; // already done
            }
        }

        RecurringBill bill = billRepo.findByIdAndUserId(billId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));

        bill.setActive(!bill.isActive());
        billRepo.save(bill);

        if (idemKey != null && !idemKey.isBlank()) {
            idemRepo.save(RecurringBillIdempotency.create(userId, idemKey, "TOGGLE", billId));
        }
    }

    @Transactional
    public void delete(Integer userId, Integer billId, String idemKey) {
        if (idemKey != null && !idemKey.isBlank()) {
            if (idemRepo.existsByUserIdAndIdemKeyAndOperation(userId, idemKey, "DELETE")) {
                return; // already done
            }
        }

        RecurringBill bill = billRepo.findByIdAndUserId(billId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));

        billRepo.delete(bill);

        if (idemKey != null && !idemKey.isBlank()) {
            idemRepo.save(RecurringBillIdempotency.create(userId, idemKey, "DELETE", billId));
        }
    }
}
