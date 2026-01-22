package com.Eqinox.store.services;

import com.Eqinox.store.dtos.SpendingAnomalyDto;
import com.Eqinox.store.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class SpendingAnomalyService {

    private final TransactionRepository transactionRepo;

    private static final BigDecimal THRESHOLD = new BigDecimal("1.5"); // 50%

    public SpendingAnomalyService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public SpendingAnomalyDto checkDailyAnomaly(
            Integer userId,
            LocalDate date) {

        BigDecimal todaySpend =
                transactionRepo.sumDailyExpense(userId, date);

        if (todaySpend.compareTo(BigDecimal.ZERO) == 0) {
            return null; // no spending → no anomaly
        }

        LocalDate start = date.minusDays(30);
        LocalDate end = date.minusDays(1);

        BigDecimal avg =
                transactionRepo.averageDailyExpense(userId, start, end);

        if (avg == null || avg.compareTo(BigDecimal.ZERO) == 0) {
            return null; // not enough data
        }

        BigDecimal ratio = todaySpend.divide(avg, 2, RoundingMode.HALF_UP);

        if (ratio.compareTo(THRESHOLD) < 0) {
            return null; // not anomalous
        }

        SpendingAnomalyDto dto = new SpendingAnomalyDto();
        dto.setDate(date);
        dto.setTodaySpend(todaySpend);
        dto.setAverageSpend(avg);

        BigDecimal percent =
                ratio.subtract(BigDecimal.ONE)
                     .multiply(new BigDecimal("100"))
                     .setScale(0, RoundingMode.HALF_UP);

        dto.setPercentageIncrease(percent);
        dto.setMessage(
                "⚠️ You spent " + percent + "% more than your daily average"
        );

        return dto;
    }
}
