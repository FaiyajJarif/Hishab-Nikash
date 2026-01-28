package com.Eqinox.store.services;

import com.Eqinox.store.dtos.alerts.OverspendAlertDto;
import com.Eqinox.store.dtos.analytics.DailyOverspendAlertDto;
import com.Eqinox.store.entities.BudgetItem;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.entities.RecurringBill;
import com.Eqinox.store.entities.Transaction;
import com.Eqinox.store.entities.TransactionType;
import com.Eqinox.store.repositories.BudgetItemRepository;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Eqinox.store.services.FamilySyncService;
import com.Eqinox.store.websocket.NotificationMessage;
import com.Eqinox.store.websocket.NotificationPublisher;
import com.Eqinox.store.dtos.UpdateTransactionRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

        public TransactionService(TransactionRepository repo, FamilySyncService familySyncService,
                        BudgetItemRepository budgetItemRepo, BudgetService budgetService,
                        TransactionRepository transactionRepo,
                        CategoryRepository categoryRepo, AlertService alertService,
                        OverspendAlertService overspendAlertService,
                        AnalyticsService analyticsService,
                        NotificationPublisher notificationPublisher) {
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
        }

        @Transactional
        public void createExpenseFromRecurringBill(RecurringBill bill) {
        
            if (bill.getCategoryId() == null || bill.getCategoryId() <= 0) {
                throw new IllegalStateException(
                    "Recurring bill " + bill.getId() + " has invalid categoryId=" + bill.getCategoryId()
                );
            }
        
            createExpense(
                bill.getUserId(),
                bill.getCategoryId(),
                bill.getAmount(),
                LocalDate.now(),
                "Auto bill: " + bill.getName()
            );
            // 🔔 Notify frontend to refresh dashboard
            notificationPublisher.notifyUser(
                bill.getUserId(),
                new NotificationMessage(
                    "DASHBOARD_REFRESH",
                    LocalDate.now(),
                    bill.getAmount(),
                    "Expense created"
                )
            );            
    
        }
        

        // ✅ CREATE EXPENSE
        // ✅ CREATE EXPENSE
        public void createExpense(
                        Integer userId,
                        Integer categoryId,
                        BigDecimal amount,
                        LocalDate date,
                        String note) {

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Expense amount must be positive");
                }

                // 1️⃣ Save transaction
                Transaction tx = new Transaction();
                tx.setUserId(userId);
                tx.setCategoryId(categoryId);
                tx.setType(TransactionType.EXPENSE);
                tx.setAmount(amount);
                tx.setDate(date);
                tx.setNote(note);

                transactionRepo.save(tx);
                overspendAlertService.checkDailyOverspend(userId, date);

                // 🔔 DAILY OVERSPEND CHECK (FIXED LOCATION)
                DailyOverspendAlertDto alert = analyticsService.getDailyOverspendAlert(userId, date);

                if (alert.isOverspent()) {
                        notificationPublisher.notifyUser(
                                        userId,
                                        new NotificationMessage(
                                                        "DAILY_OVERSPEND",
                                                        date,
                                                        alert.getTodayExpense(),
                                                        "You overspent today by " + alert.getDifference()));
                }

                // 2️⃣ Get budget period
                BudgetPeriod period = budgetService.getOrCreateBudget(
                                userId,
                                date.getMonthValue(),
                                date.getYear());

                // 3️⃣ Ensure budget items exist
                budgetService.syncBudgetItems(period);

                // 4️⃣ Update actual amount
                List<BudgetItem> items = budgetItemRepo.findAllByPeriodAndCategory(period, categoryId);

                if (items.isEmpty()) {
                        throw new RuntimeException("Budget item not found");
                }

                BudgetItem item = items.get(0);

                BigDecimal currentActual = item.getActualAmount() == null
                                ? BigDecimal.ZERO
                                : item.getActualAmount();

                item.setActualAmount(currentActual.add(amount));

                BigDecimal planned = item.getPlannedAmount() == null
                                ? BigDecimal.ZERO
                                : item.getPlannedAmount();

                BigDecimal actual = item.getActualAmount();

                if (actual.compareTo(planned) > 0) {
                        BigDecimal overBy = actual.subtract(planned);
                        String catName = categoryRepo.findById(categoryId)
                                        .map(c -> c.getName())
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
                                                        "Overspent " + catName + " by " + overBy));
                }

                budgetItemRepo.save(item);
        }

        public Transaction updateTransaction(Integer userId, Integer txId,
                        com.Eqinox.store.dtos.UpdateTransactionRequest req) {
                Transaction tx = repo.findById(txId)
                                .orElseThrow(() -> new RuntimeException("Transaction not found"));

                if (!tx.getUserId().equals(userId)) {
                        throw new RuntimeException("Unauthorized");
                }

                if (req.getAmount() == null || req.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                }

                tx.setCategoryId(req.getCategoryId());
                tx.setAmount(req.getAmount());
                tx.setDate(req.getDate());
                tx.setNote(req.getNote());

                Transaction saved = repo.save(tx);

                // (optional) broadcast to family if you want
                familySyncService.broadcastUserChange(
                                userId,
                                "TRANSACTION_UPDATED",
                                "💸 A family member updated a transaction");

                return saved;
        }

        public void deleteTransaction(Integer userId, Integer txId) {
                Transaction tx = repo.findById(txId)
                                .orElseThrow(() -> new RuntimeException("Transaction not found"));

                if (!tx.getUserId().equals(userId)) {
                        throw new RuntimeException("Unauthorized");
                }

                repo.delete(tx);

                familySyncService.broadcastUserChange(
                                userId,
                                "TRANSACTION_DELETED",
                                "🗑️ A family member deleted a transaction");
        }

        public java.util.List<Transaction> getRecentTransactions(Integer userId, int limit) {
                int safeLimit = Math.max(1, Math.min(limit, 50));
                return repo.findRecentByUserId(userId, org.springframework.data.domain.PageRequest.of(
                                0,
                                safeLimit,
                                org.springframework.data.domain.Sort
                                                .by(org.springframework.data.domain.Sort.Direction.DESC, "date")));
        }

}
