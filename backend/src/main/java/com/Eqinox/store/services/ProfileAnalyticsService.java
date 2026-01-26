package com.Eqinox.store.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import com.Eqinox.store.dtos.DashboardSummaryDto;
import com.Eqinox.store.dtos.analytics.MonthSummaryDto;
import com.Eqinox.store.dtos.profile.FinancialPersonaDto;
import com.Eqinox.store.dtos.profile.PeerComparisonDto;
import com.Eqinox.store.dtos.profile.TrendDeltaDto;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.UserRepository;

@Service
public class ProfileAnalyticsService {

    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;

    public ProfileAnalyticsService(
            UserRepository userRepository,
            BudgetService budgetService,
            AnalyticsService analyticsService
    ) {
        this.userRepository = userRepository;
        this.budgetService = budgetService;
        this.analyticsService = analyticsService;
    }

    public PeerComparisonDto peerComparison(Integer userId, int month, int year) {
        User user = userRepository.findById(userId).orElseThrow();

        int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
        String ageRange = age < 26 ? "18–25"
                : age < 36 ? "26–35"
                : age < 46 ? "36–45"
                : "46+";

        double yourSpending = analyticsService.totalSpending(userId, month, year);
        double peerAvg = analyticsService.avgSpendingForAgeRange(ageRange, month, year);

        double diffPct = peerAvg == 0 ? 0 :
                ((yourSpending - peerAvg) / peerAvg) * 100;

        return new PeerComparisonDto(
                ageRange,
                yourSpending,
                peerAvg,
                diffPct
        );
    }

    public FinancialPersonaDto persona(Integer userId, int month, int year) {
        DashboardSummaryDto s = budgetService.getSummary(userId, month, year);

        BigDecimal remaining = s.getRemaining();
        BigDecimal income = s.getIncome();

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            return new FinancialPersonaDto(
                "Over-spender", "🔴", "You often exceed your budget"
            );
        }

        BigDecimal threshold = income.multiply(BigDecimal.valueOf(0.2));

        if (remaining.compareTo(threshold) > 0) {
            return new FinancialPersonaDto(
                "Saver", "🟢", "You consistently save money"
            );
        }

        return new FinancialPersonaDto(
            "Balanced", "🔵", "You manage your budget well"
        );
    }

    public TrendDeltaDto trend(Integer userId, int month, int year) {
        MonthSummaryDto current = analyticsService.getMonthSummary(userId, month, year);      
        int prevMonth = month == 1 ? 12 : month - 1;
            int prevYear = month == 1 ? year - 1 : year;

            MonthSummaryDto prev =
                    analyticsService.getMonthSummary(userId, prevMonth, prevYear);
                    if (current == null || prev == null) {
                        return new TrendDeltaDto(0, 0, 0);
                    }  
                    

                    return new TrendDeltaDto(
                        pct(current.getIncome(), prev.getIncome()),
                        pct(current.getExpense(), prev.getExpense()),
                        pct(current.getRemaining(), prev.getRemaining())
                    );                    
    }
    private double pct(BigDecimal now, BigDecimal prev) {
        if (prev == null || prev.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return now
            .subtract(prev)
            .divide(prev, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(1, RoundingMode.HALF_UP)
            .doubleValue();
    }
        
}
