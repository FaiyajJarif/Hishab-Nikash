package com.Eqinox.store.services;

import com.Eqinox.store.dtos.UpdateTransactionRequest;
import com.Eqinox.store.dtos.alerts.OverspendAlertDto;
import com.Eqinox.store.dtos.analytics.DailyOverspendAlertDto;
import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.BudgetItemRepository;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.TransactionRepository;
import com.Eqinox.store.websocket.NotificationMessage;
import com.Eqinox.store.websocket.NotificationPublisher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository repo;
    private final FamilySyncService familySyncService;
    private final BudgetItemRepository budgetItemRepo;
    private final BudgetService budgetService;
    private final TransactionRepository transactionRepo;
    private final CategoryRepository categoryRepo;
    private final AlertService alertService;
    private final OverspendAlertService overspendAlertService;
    private final AnalyticsService analyticsService;
    private final NotificationPublisher notificationPublisher;
    private final WalletService walletService;   // ✅ NEW
    private final BankSyncService bankService;

    public TransactionService(
            TransactionRepository repo,
            FamilySyncService familySyncService,
            BudgetItemRepository budgetItemRepo,
            BudgetService budgetService,
            TransactionRepository transactionRepo,
            CategoryRepository categoryRepo,
            AlertService alertService,
            OverspendAlertService overspendAlertService,
            AnalyticsService analyticsService,
            NotificationPublisher notificationPublisher,
            WalletService walletService,
            BankSyncService bankService     // ✅ NEW
    ) {
        this.repo = repo;
        this.familySyncService = familySyncService;
        this.budgetItemRepo = budgetItemRepo;
        this.budgetService = budgetService;
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.alertService = alertService;
        this.overspendAlertService = overspendAlertService;
        this.analyticsService = analyticsService;
        this.notificationPublisher = notificationPublisher;
        this.walletService = walletService;  
        this.bankService = bankService; // ✅ NEW
    }

    // ==========================================
    // 🔥 NEW CREATE EXPENSE (WITH PAYMENT METHOD)
    // ==========================================
    public void createExpense(
            Integer userId,
            Integer categoryId,
            BigDecimal amount,
            LocalDate date,
            String note,
            String paymentMethodRaw
    ) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }

        PaymentMethod paymentMethod;

        try {
            paymentMethod = PaymentMethod.valueOf(paymentMethodRaw.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid payment method");
        }

        // 🔥 WALLET DEDUCTION
        // 🔥 PAYMENT HANDLING
        if (paymentMethod == PaymentMethod.WALLET) {

                walletService.deduct(
                        userId,
                        amount,
                        "Expense: " + (note != null ? note : "")
                );
        
        } else if (paymentMethod == PaymentMethod.BANK) {
        
                bankService.deductFromDefaultBank(userId, amount);
        
        }    

        // 1️⃣ Save transaction
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setCategoryId(categoryId);
        tx.setType(TransactionType.EXPENSE);
        tx.setAmount(amount);
        tx.setDate(date);
        tx.setNote(note);
        tx.setPaymentMethod(paymentMethod);

        transactionRepo.save(tx);


        // 🔔 DAILY OVERSPEND CHECK
        overspendAlertService.checkDailyOverspend(userId, date);

        DailyOverspendAlertDto alert =
                analyticsService.getDailyOverspendAlert(userId, date);

        if (alert.isOverspent()) {
            notificationPublisher.notifyUser(
                    userId,
                    new NotificationMessage(
                            "DAILY_OVERSPEND",
                            date,
                            alert.getTodayExpense(),
                            "You overspent today by " + alert.getDifference()
                    )
            );
        }

        // 2️⃣ Update budget
        BudgetPeriod period = budgetService.getOrCreateBudget(
                userId,
                date.getMonthValue(),
                date.getYear()
        );

        budgetService.syncBudgetItems(period);

        budgetItemRepo.addActualAmount(
                userId,
                categoryId,
                date.getMonthValue(),
                date.getYear(),
                amount
        );
        
        List<BudgetItem> items =
        budgetItemRepo.findAllByPeriodAndCategory(period, categoryId);

        if (items.isEmpty()) {
        throw new RuntimeException("Budget item not found");
        }

        BudgetItem item = items.get(0);

        BigDecimal planned = item.getPlannedAmount() == null
                ? BigDecimal.ZERO
                : item.getPlannedAmount();

        BigDecimal actual = budgetItemRepo.sumActualForPeriod(period);
        if (actual == null) actual = BigDecimal.ZERO;
                

        // 🔔 CATEGORY OVERSPEND CHECK
        if (actual.compareTo(planned) > 0) {

            BigDecimal overBy = actual.subtract(planned);

            String catName =
                    categoryRepo.findById(categoryId)
                            .map(Category::getName)
                            .orElse("Unknown");

            alertService.sendToUser(
                    userId,
                    new OverspendAlertDto(
                            "CATEGORY_OVERSPEND",
                            categoryId,
                            catName,
                            planned,
                            actual,
                            overBy,
                            date,
                            "Overspent " + catName + " by " + overBy
                    )
            );
        }

        budgetService.recalcTotals(period);
    }

    // ==========================================
    // ✅ OLD METHOD (BACKWARD COMPATIBLE)
    // ==========================================
    public void createExpense(
            Integer userId,
            Integer categoryId,
            BigDecimal amount,
            LocalDate date,
            String note
    ) {
        createExpense(userId, categoryId, amount, date, note, "WALLET");
    }

    // ==========================================
    // 🔁 RECURRING BILL SUPPORT
    // ==========================================
    @Transactional
        public void createExpenseFromRecurringBill(RecurringBill bill) {

        if (bill.getCategoryId() == null || bill.getCategoryId() <= 0) {
                throw new IllegalStateException(
                        "Recurring bill " + bill.getId() +
                                " has invalid categoryId=" + bill.getCategoryId()
                );
        }

        // 🔥 CHECK WALLET BALANCE FIRST
        Wallet wallet = walletService.getWallet(bill.getUserId());

        if (wallet.getBalance().compareTo(bill.getAmount()) >= 0) {

                // ✅ Enough balance → create expense
                createExpense(
                        bill.getUserId(),
                        bill.getCategoryId(),
                        bill.getAmount(),
                        LocalDate.now(),
                        "Auto bill: " + bill.getName(),
                        "WALLET"
                );

                notificationPublisher.notifyUser(
                        bill.getUserId(),
                        new NotificationMessage(
                                "DASHBOARD_REFRESH",
                                LocalDate.now(),
                                bill.getAmount(),
                                "Recurring bill paid successfully"
                        )
                );

        } else {

                // ❌ Not enough balance → notify failure
                notificationPublisher.notifyUser(
                        bill.getUserId(),
                        new NotificationMessage(
                                "BILL_FAILED",
                                LocalDate.now(),
                                bill.getAmount(),
                                "Recurring bill failed: insufficient wallet balance"
                        )
                );
        }
        }

    // ==========================================
    // UPDATE TRANSACTION
    // ==========================================
    public Transaction updateTransaction(Integer userId, Integer txId,
                                         UpdateTransactionRequest req) {

        Transaction tx = repo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!tx.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (req.getAmount() == null ||
                req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        BigDecimal oldAmount = tx.getAmount();
        LocalDate oldDate = tx.getDate();
        Integer oldCategoryId = tx.getCategoryId();

        tx.setCategoryId(req.getCategoryId());
        tx.setAmount(req.getAmount());
        tx.setDate(req.getDate());
        tx.setNote(req.getNote());

        Transaction saved = repo.save(tx);

        // If category or month changed → full remove + full add
if (!oldCategoryId.equals(req.getCategoryId())
        || oldDate.getMonthValue() != req.getDate().getMonthValue()
        || oldDate.getYear() != req.getDate().getYear()) {

    // remove from old
    budgetItemRepo.addActualAmount(
            userId,
            oldCategoryId,
            oldDate.getMonthValue(),
            oldDate.getYear(),
            oldAmount.negate()
    );

    // add to new
        budgetItemRepo.addActualAmount(
                userId,
                req.getCategoryId(),
                req.getDate().getMonthValue(),
                req.getDate().getYear(),
                req.getAmount()
        );

        } else {

        // same category + month → only difference
        BigDecimal difference = req.getAmount().subtract(oldAmount);

        budgetItemRepo.addActualAmount(
                userId,
                req.getCategoryId(),
                req.getDate().getMonthValue(),
                req.getDate().getYear(),
                difference
        );
        }

        BudgetPeriod period = budgetService.getOrCreateBudget(
                userId,
                req.getDate().getMonthValue(),
                req.getDate().getYear()
        );

        budgetService.recalcTotals(period);

        familySyncService.broadcastUserChange(
                userId,
                "TRANSACTION_UPDATED",
                "💸 A family member updated a transaction"
        );

        return saved;
    }

    // ==========================================
    // DELETE TRANSACTION
    // ==========================================
    public void deleteTransaction(Integer userId, Integer txId) {

        Transaction tx = repo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!tx.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        LocalDate date = tx.getDate();
        BigDecimal amount = tx.getAmount();
        Integer categoryId = tx.getCategoryId();

        // subtract from budget
        budgetItemRepo.addActualAmount(
                userId,
                categoryId,
                date.getMonthValue(),
                date.getYear(),
                amount.negate()
        );

        BudgetPeriod period = budgetService.getOrCreateBudget(
                userId,
                date.getMonthValue(),
                date.getYear()
        );

        budgetService.recalcTotals(period);


        repo.delete(tx);

        familySyncService.broadcastUserChange(
                userId,
                "TRANSACTION_DELETED",
                "🗑️ A family member deleted a transaction"
        );
    }

    // ==========================================
    // RECENT TRANSACTIONS
    // ==========================================
    public List<Transaction> getRecentTransactions(Integer userId, int limit) {

        int safeLimit = Math.max(1, Math.min(limit, 50));

        return repo.findRecentByUserId(
                userId,
                org.springframework.data.domain.PageRequest.of(
                        0,
                        safeLimit,
                        org.springframework.data.domain.Sort
                                .by(org.springframework.data.domain.Sort.Direction.DESC, "date")
                )
        );
    }
}
