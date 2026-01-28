package com.Eqinox.store.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Eqinox.store.dtos.CashflowPointDto;
import com.Eqinox.store.dtos.alerts.AlertEventDto;
import com.Eqinox.store.dtos.analytics.CalendarDayDto;
import com.Eqinox.store.dtos.analytics.CategoryBreakdownDto;
import com.Eqinox.store.dtos.analytics.CategoryOverspendDto;
import com.Eqinox.store.dtos.analytics.DailyCashflowDto;
import com.Eqinox.store.dtos.analytics.DailyOverspendAlertDto;
import com.Eqinox.store.dtos.analytics.DailySummaryDto;
import com.Eqinox.store.dtos.analytics.DayDetailDto;
import com.Eqinox.store.dtos.analytics.InsightDto;
import com.Eqinox.store.dtos.analytics.MonthSummaryDto;
import com.Eqinox.store.dtos.analytics.MonthTrendDto;
import com.Eqinox.store.dtos.analytics.RollingAverageDto;
import com.Eqinox.store.dtos.analytics.TransactionListItemDto;
import com.Eqinox.store.entities.Alert;
import com.Eqinox.store.entities.BudgetItem;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.entities.Category;
import com.Eqinox.store.entities.MonthlyAnalyticsSnapshot;
import com.Eqinox.store.entities.TransactionType;
import com.Eqinox.store.repositories.AlertRepository;
import com.Eqinox.store.repositories.AnalyticsTransactionRepository;
import com.Eqinox.store.repositories.CategoryRepository;
import com.Eqinox.store.repositories.MonthlyAnalyticsSnapshotRepository;
import com.Eqinox.store.repositories.TransactionRepository;
import com.Eqinox.store.repositories.BudgetItemRepository;
import com.Eqinox.store.entities.Transaction;
import com.Eqinox.store.entities.TransactionType;
import com.Eqinox.store.exceptions.BusinessException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AnalyticsService {

    private final AnalyticsTransactionRepository txRepo;
    private final CategoryRepository categoryRepo;
    private final MonthlyAnalyticsSnapshotRepository snapshotRepo;
    private final BudgetService budgetService;
    private final TransactionRepository transactionRepo;
    private final BudgetItemRepository budgetItemRepo;
    private final AlertRepository alertRepository;
    private final UserWsNotificationService userNotificationService;

    public AnalyticsService(
            AnalyticsTransactionRepository txRepo,
            CategoryRepository categoryRepo,
            MonthlyAnalyticsSnapshotRepository snapshotRepo,
            BudgetService budgetService,
            TransactionRepository transactionRepo,
            BudgetItemRepository budgetItemRepo,
            AlertRepository alertRepository,
            UserWsNotificationService userWsNotificationService) {
        this.txRepo = txRepo;
        this.categoryRepo = categoryRepo;
        this.snapshotRepo = snapshotRepo;
        this.budgetService = budgetService;
        this.transactionRepo = transactionRepo;
        this.budgetItemRepo = budgetItemRepo;
        this.alertRepository = alertRepository;
        this.userNotificationService = userWsNotificationService;
    }

    // --------------------
    // DAILY
    // --------------------
    public DailySummaryDto getDailySummary(Integer userId, LocalDate date) {
        BigDecimal income = txRepo.sumByDayAndType(userId, date, TransactionType.INCOME);
        BigDecimal expense = txRepo.sumByDayAndType(userId, date, TransactionType.EXPENSE);
        BigDecimal net = income.subtract(expense);
        long count = txRepo.countByDay(userId, date);

        return new DailySummaryDto(date, income, expense, net, count);
    }

    public List<CategoryBreakdownDto> getDailyExpenseByCategory(Integer userId, LocalDate date) {
        // Simple approach: reuse monthly method with same start/end
        LocalDate start = date;
        LocalDate end = date;

        List<Object[]> rows = txRepo.monthExpenseByCategory(userId, start, end);
        Map<Integer, String> categoryNames = categoryNameMap(userId);

        List<CategoryBreakdownDto> out = new ArrayList<>();
        for (Object[] r : rows) {
            Integer categoryId = (Integer) r[0];
            BigDecimal amount = (BigDecimal) r[1];
            out.add(new CategoryBreakdownDto(
                    categoryId,
                    categoryNames.getOrDefault(categoryId, "Unknown"),
                    amount));
        }
        return out;
    }

    public List<CalendarDayDto> getCalendarHeatmap(Integer userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Object[]> rows = txRepo.dailyExpenseTotals(userId, start, end);
        List<CalendarDayDto> out = new ArrayList<>();

        for (Object[] r : rows) {
            LocalDate d = (LocalDate) r[0];
            BigDecimal amt = (BigDecimal) r[1];
            out.add(new CalendarDayDto(d, amt));
        }
        return out;
    }

    // --------------------
    // MONTHLY
    // --------------------
    public MonthSummaryDto getMonthSummary(Integer userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal income = txRepo.sumByRangeAndType(
                userId, start, end, TransactionType.INCOME);

        BigDecimal expense = txRepo.sumByRangeAndType(
                userId, start, end, TransactionType.EXPENSE);

        BigDecimal net = income.subtract(expense);

        BudgetPeriod period = budgetService.getOrCreateBudget(userId, month, year);
        BigDecimal assigned = budgetService.getAssignedTotal(period);

        BigDecimal remaining = period.getIncome().subtract(assigned);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {

            String msg = "🚨 You overspent this month by ৳" + remaining.abs();

            boolean exists = alertRepository
                .existsByUserIdAndMessageAndCreatedAtAfter(
                    userId,
                    msg,
                    LocalDateTime.now().minusHours(24)
                );

            if (!exists) {
                Alert alert = new Alert(
                    userId,
                    msg,
                    "CRITICAL"
                );
                
                alertRepository.save(alert);
                System.out.println("🔥 FORCING WS SEND");
                userNotificationService.broadcastToUser(
                    userId,
                    new AlertEventDto(
                        alert.getMessage(),
                        alert.getSeverity(),
                        alert.getCreatedAt().atOffset(ZoneOffset.UTC)
                    )
                );                
            }
        }       
        BigDecimal unassigned = remaining;

        return new MonthSummaryDto(month, year, period.getIncome(), expense, net, assigned, remaining, unassigned);
    }

    public List<CategoryBreakdownDto> getMonthExpenseByCategory(Integer userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Object[]> rows = txRepo.monthExpenseByCategory(userId, start, end);
        Map<Integer, String> categoryNames = categoryNameMap(userId);

        List<CategoryBreakdownDto> out = new ArrayList<>();
        for (Object[] r : rows) {
            Integer categoryId = (Integer) r[0];
            BigDecimal amount = (BigDecimal) r[1];
            out.add(new CategoryBreakdownDto(
                    categoryId,
                    categoryNames.getOrDefault(categoryId, "Unknown"),
                    amount));
        }

        // Optional: sort desc for nicer charts
        out.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        return out;
    }

    public MonthTrendDto getMonthWeeklyTrend(Integer userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // Build week buckets: Week 1 = days 1-7, Week 2 = 8-14, ...
        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        int day = 1;
        while (day <= end.getDayOfMonth()) {
            int from = day;
            int to = Math.min(day + 6, end.getDayOfMonth());

            LocalDate s = ym.atDay(from);
            LocalDate e = ym.atDay(to);

            BigDecimal weekExpense = txRepo.sumByRangeAndType(
                    userId, s, e, TransactionType.EXPENSE);

            labels.add("Week " + ((from - 1) / 7 + 1));
            values.add(weekExpense);

            day += 7;
        }

        return new MonthTrendDto(labels, values);
    }

    // --------------------
    // MONTH CLOSE (SNAPSHOT)
    // --------------------
    public MonthlyAnalyticsSnapshot closeMonth(Integer userId, int month, int year) {

        MonthlyAnalyticsSnapshot snap = snapshotRepo
                .findByUserIdAndMonthAndYear(userId, month, year)
                .orElse(null);

        if (snap != null && snap.getClosedAt() != null) {
            throw new BusinessException("Month already closed");
        }

        MonthSummaryDto summary = getMonthSummary(userId, month, year);

        if (snap == null) {
            snap = new MonthlyAnalyticsSnapshot();
            snap.setUserId(userId);
            snap.setMonth(month);
            snap.setYear(year);
            snap.setCreatedAt(OffsetDateTime.now());
        }

        snap.setIncome(summary.getIncome());
        snap.setExpense(summary.getExpense());
        snap.setAssigned(summary.getAssigned());
        snap.setUnassigned(summary.getUnassigned());
        snap.setClosedAt(OffsetDateTime.now()); // ⭐ THIS is the close

        return snapshotRepo.save(snap);
    }

    public void reopenMonth(Integer userId, int month, int year) {

        MonthlyAnalyticsSnapshot snap = snapshotRepo
                .findByUserIdAndMonthAndYear(userId, month, year)
                .orElseThrow(() -> new BusinessException("Month not found"));
    
        if (snap.getClosedAt() == null) {
            throw new BusinessException("Month is not closed");
        }
    
        snap.setClosedAt(null);
        snapshotRepo.save(snap);
    }    
    // --------------------
    // Helpers
    // --------------------
    private Map<Integer, String> categoryNameMap(Integer userId) {
        List<Category> categories = categoryRepo.findByUserId(userId);
        Map<Integer, String> map = new HashMap<>();
        for (Category c : categories) {
            map.put(c.getCategoryId(), c.getName());
        }
        return map;
    }

    public BigDecimal getMonthLeftover(Integer userId, int month, int year) {
        BudgetPeriod period = budgetService.getOrCreateBudget(userId, month, year);

        BigDecimal income = period.getIncome() == null ? BigDecimal.ZERO : period.getIncome();
        BigDecimal assigned = budgetService.getAssignedTotal(period);

        return income.subtract(assigned);
    }

    public List<RollingAverageDto> getRollingDailyExpense(
            Integer userId,
            int days,
            LocalDate endDate) {

        List<RollingAverageDto> out = new ArrayList<>();

        for (int i = days; i >= 1; i--) {
            LocalDate end = endDate.minusDays(i - 1);
            LocalDate start = end.minusDays(days - 1);

            BigDecimal avg = txRepo.sumByRangeAndType(
                    userId,
                    start,
                    end,
                    TransactionType.EXPENSE).divide(
                            BigDecimal.valueOf(days),
                            2,
                            BigDecimal.ROUND_HALF_UP);

            out.add(new RollingAverageDto(end, avg));
        }
        return out;
    }

    // Alerts
    public DailyOverspendAlertDto getDailyOverspendAlert(
            Integer userId,
            LocalDate date) {
        // Today expense
        BigDecimal todayExpense = txRepo.sumByDayAndType(
                userId,
                date,
                TransactionType.EXPENSE);

        // Month range
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

        // Total month expense
        BigDecimal monthExpense = txRepo.sumByRangeAndType(
                userId,
                start,
                end,
                TransactionType.EXPENSE);

        // Count days with expenses
        long expenseDays = txRepo
                .dailyExpenseTotals(userId, start, end)
                .size();

        if (expenseDays == 0) {
            return new DailyOverspendAlertDto(
                    date,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    false,
                    BigDecimal.ZERO);
        }

        BigDecimal averageDaily = monthExpense.divide(
                BigDecimal.valueOf(expenseDays),
                2,
                BigDecimal.ROUND_HALF_UP);

        boolean overspent = todayExpense.compareTo(averageDaily) > 0;

        BigDecimal difference = overspent
                ? todayExpense.subtract(averageDaily)
                : BigDecimal.ZERO;

        return new DailyOverspendAlertDto(
                date,
                todayExpense,
                averageDaily,
                overspent,
                difference);
    }

    public List<CategoryOverspendDto> getCategoryOverspend(
            Integer userId,
            int month,
            int year) {
        BudgetPeriod period = budgetService.getOrCreateBudget(userId, month, year);

        List<BudgetItem> items = budgetItemRepo.findByBudgetPeriod(period);
        Map<Integer, String> categoryNames = categoryNameMap(userId);

        List<CategoryOverspendDto> result = new ArrayList<>();

        for (BudgetItem item : items) {
            BigDecimal planned = item.getPlannedAmount();
            BigDecimal actual = item.getActualAmount();

            if (actual.compareTo(planned) > 0) {
                BigDecimal diff = actual.subtract(planned);

                result.add(new CategoryOverspendDto(
                        item.getCategoryId(),
                        categoryNames.getOrDefault(item.getCategoryId(), "Unknown"),
                        planned,
                        actual,
                        diff,
                        true));
                        String msg =
                            "⚠️ You overspent in \"" +
                            categoryNames.getOrDefault(item.getCategoryId(), "Unknown") +
                            "\" by ৳" + diff;

                        boolean exists = alertRepository
                            .existsByUserIdAndMessageAndCreatedAtAfter(
                                userId,
                                msg,
                                LocalDateTime.now().minusHours(24)
                            );

                        if (!exists) {
                            alertRepository.save(new Alert(userId, msg, "WARNING"));
                        }
            }
        }

        return result;
    }

    public InsightDto getDailyInsight(Integer userId, LocalDate date) {
        BigDecimal today = transactionRepo.sumDailyExpense(userId, date);

        LocalDate start = date.minusDays(14);
        LocalDate end = date.minusDays(1);

        BigDecimal avg = transactionRepo.averageDailyExpense(userId, start, end);
        if (avg == null)
            avg = BigDecimal.ZERO;

        BigDecimal diff = today.subtract(avg);

        String msg;
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            msg = "You spent " + diff + " more than your 14-day daily average.";
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            msg = "You spent " + diff.abs() + " less than your 14-day daily average.";
        } else {
            msg = "You spent exactly your 14-day daily average.";
        }

        return new InsightDto("DAILY", date, null, today, avg, diff, msg);
    }

    public InsightDto getCategoryInsight(Integer userId, Integer categoryId, LocalDate date) {
        BigDecimal today = transactionRepo.sumDailyCategoryExpense(userId, categoryId, date);

        LocalDate start = date.minusDays(14);
        LocalDate end = date.minusDays(1);

        BigDecimal avg = transactionRepo.averageDailyCategoryExpense(userId, categoryId, start, end);
        if (avg == null)
            avg = BigDecimal.ZERO;

        BigDecimal diff = today.subtract(avg);

        String msg;
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            msg = "Category spending is " + diff + " above your 14-day category average.";
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            msg = "Category spending is " + diff.abs() + " below your 14-day category average.";
        } else {
            msg = "Category spending matches your 14-day category average.";
        }

        return new InsightDto("CATEGORY", date, categoryId, today, avg, diff, msg);
    }

    public DayDetailDto getDayDetail(Integer userId, LocalDate date) {

        // summary (use your TransactionRepository methods OR analytics repo)
        BigDecimal income = transactionRepo.sumDailyIncome(userId, date);
        BigDecimal expense = transactionRepo.sumDailyExpense(userId, date);
        BigDecimal net = income.subtract(expense);

        // categories breakdown (you already wrote this)
        List<Object[]> catRows = transactionRepo.dailyCategoryBreakdown(userId, date);

        Map<Integer, String> categoryNames = categoryNameMap(userId);

        // convert breakdown
        List<CategoryBreakdownDto> categories = new ArrayList<>();
        for (Object[] row : catRows) {
            Integer categoryId = (Integer) row[0];
            String catName = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            categories.add(new CategoryBreakdownDto(categoryId, catName, amount));

        }

        // transactions list
        List<Transaction> txs = transactionRepo.findAllForDay(userId, date);

        List<TransactionListItemDto> transactions = new ArrayList<>();
        for (Transaction t : txs) {
            String catName = categoryNames.getOrDefault(t.getCategoryId(), "Unknown");
            transactions.add(new TransactionListItemDto(
                t.getTransactionId(),
                    t.getCategoryId(),
                    catName,
                    t.getType(),
                    t.getAmount(),
                    t.getDate(),
                    t.getNote()));
        }

        long count = txs.size();

        return new DayDetailDto(date, income, expense, net, count, categories, transactions);
    }
    public List<MonthlyAnalyticsSnapshot> getMonthHistory(Integer userId) {
        return snapshotRepo.findByUserIdOrderByYearDescMonthDesc(userId);
    }    
    public double totalSpending(Integer userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
    
        BigDecimal expense = txRepo.sumByRangeAndType(
                userId, start, end, TransactionType.EXPENSE);
    
        return expense == null ? 0 : expense.doubleValue();
    }
    public double avgSpendingForAgeRange(String ageRange, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
    
        int minAge;
        int maxAge;
    
        switch (ageRange) {
            case "18–25" -> { minAge = 18; maxAge = 25; }
            case "26–35" -> { minAge = 26; maxAge = 35; }
            case "36–45" -> { minAge = 36; maxAge = 45; }
            default -> { minAge = 46; maxAge = 200; }
        }
    
        BigDecimal avg =
                txRepo.avgSpendingForAgeRange(minAge, maxAge, start, end);
    
        return avg == null ? 0 : avg.doubleValue();
    }      

    public List<CashflowPointDto> getCashflow(
        Integer userId,
        int monthsBack
) {
    List<CashflowPointDto> out = new ArrayList<>();
    YearMonth now = YearMonth.now();

    for (int i = monthsBack - 1; i >= 0; i--) {
        YearMonth ym = now.minusMonths(i);

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal income = txRepo.sumByRangeAndType(
            userId, start, end, TransactionType.INCOME
        );

        BigDecimal expense = txRepo.sumByRangeAndType(
            userId, start, end, TransactionType.EXPENSE
        );

        out.add(new CashflowPointDto(
            ym.getMonth().name().substring(0, 3),
            income == null ? BigDecimal.ZERO : income,
            expense == null ? BigDecimal.ZERO : expense
        ));
    }
    return out;
}
public List<DailyCashflowDto> getDailyCashflow(Integer userId, int days) {
    LocalDate end = LocalDate.now();
    LocalDate start = end.minusDays(days - 1);

    Map<LocalDate, Double> expenses = toMap(
        transactionRepo.sumExpensesPerDay(userId, start, end)
    );

    Map<LocalDate, Double> incomes = toMap(
        transactionRepo.sumIncomePerDay(userId, start, end)
    );

    List<DailyCashflowDto> result = new ArrayList<>();

    for (int i = 0; i < days; i++) {
        LocalDate date = start.plusDays(i);

        result.add(new DailyCashflowDto(
            date,
            expenses.getOrDefault(date, 0.0),
            incomes.getOrDefault(date, 0.0)
        ));
    }

    return result;
}

private Map<LocalDate, Double> toMap(List<Object[]> rows) {
    Map<LocalDate, Double> map = new HashMap<>();
    for (Object[] r : rows) {
        map.put((LocalDate) r[0], ((Number) r[1]).doubleValue());
    }
    return map;
}
}