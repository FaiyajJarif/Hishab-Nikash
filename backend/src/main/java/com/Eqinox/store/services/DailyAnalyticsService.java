package com.Eqinox.store.services;

import com.Eqinox.store.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class DailyAnalyticsService {

    private final TransactionRepository transactionRepo;

    public DailyAnalyticsService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    // 📅 Single day summary
    public BigDecimal getDailySpend(Integer userId, LocalDate date) {
        return transactionRepo.sumDailyExpense(userId, date);
    }

    // 🗓 Calendar month view
    public Map<LocalDate, BigDecimal> getMonthlyCalendar(
            Integer userId,
            int month,
            int year) {

        List<Object[]> rows =
                transactionRepo.dailyTotalsForMonth(userId, month, year);

        Map<LocalDate, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            map.put((LocalDate) r[0], (BigDecimal) r[1]);
        }
        return map;
    }

    // 🧾 Category breakdown per day
    public Map<String, BigDecimal> getDailyCategories(
            Integer userId,
            LocalDate date) {

        List<Object[]> rows =
                transactionRepo.dailyCategoryBreakdown(userId, date);

        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] r : rows) {
            map.put((String) r[0], (BigDecimal) r[1]);
        }
        return map;
    }
}
