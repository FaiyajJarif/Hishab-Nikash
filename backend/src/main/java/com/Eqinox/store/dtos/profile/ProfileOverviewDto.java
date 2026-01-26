package com.Eqinox.store.dtos.profile;

import com.Eqinox.store.dtos.DashboardSummaryDto;
import com.Eqinox.store.dtos.UserDto;
import com.Eqinox.store.dtos.analytics.MonthSummaryDto;
import com.Eqinox.store.entities.Alert;

import java.util.List;

public class ProfileOverviewDto {

    private UserDto user;
    private DashboardSummaryDto budgetSummary;
    private MonthSummaryDto monthAnalytics;
    private TrendDeltaDto trendDelta;
    private PeerComparisonDto peerComparison;
    private FinancialPersonaDto persona;
    private List<Alert> latestAlerts;

    public ProfileOverviewDto() {
    }

    public ProfileOverviewDto(
            UserDto user,
            DashboardSummaryDto budgetSummary,
            MonthSummaryDto monthAnalytics,
            TrendDeltaDto trendDelta,
            PeerComparisonDto peerComparison,
            FinancialPersonaDto persona,
            List<Alert> latestAlerts
    ) {
        this.user = user;
        this.budgetSummary = budgetSummary;
        this.monthAnalytics = monthAnalytics;
        this.trendDelta = trendDelta;
        this.peerComparison = peerComparison;
        this.persona = persona;
        this.latestAlerts = latestAlerts;
    }

    // -------- getters --------

    public UserDto getUser() {
        return user;
    }

    public DashboardSummaryDto getBudgetSummary() {
        return budgetSummary;
    }

    public MonthSummaryDto getMonthAnalytics() {
        return monthAnalytics;
    }

    public TrendDeltaDto getTrendDelta() {
        return trendDelta;
    }

    public PeerComparisonDto getPeerComparison() {
        return peerComparison;
    }

    public FinancialPersonaDto getPersona() {
        return persona;
    }

    public List<Alert> getLatestAlerts() {
        return latestAlerts;
    }

    // -------- setters --------

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setBudgetSummary(DashboardSummaryDto budgetSummary) {
        this.budgetSummary = budgetSummary;
    }

    public void setMonthAnalytics(MonthSummaryDto monthAnalytics) {
        this.monthAnalytics = monthAnalytics;
    }

    public void setTrendDelta(TrendDeltaDto trendDelta) {
        this.trendDelta = trendDelta;
    }

    public void setPeerComparison(PeerComparisonDto peerComparison) {
        this.peerComparison = peerComparison;
    }

    public void setPersona(FinancialPersonaDto persona) {
        this.persona = persona;
    }

    public void setLatestAlerts(List<Alert> latestAlerts) {
        this.latestAlerts = latestAlerts;
    }
}
