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
    private List<Alert> latestAlerts;

    public ProfileOverviewDto() {}

    public ProfileOverviewDto(
            UserDto user,
            DashboardSummaryDto budgetSummary,
            MonthSummaryDto monthAnalytics,
            List<Alert> latestAlerts
    ) {
        this.user = user;
        this.budgetSummary = budgetSummary;
        this.monthAnalytics = monthAnalytics;
        this.latestAlerts = latestAlerts;
    }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public DashboardSummaryDto getBudgetSummary() { return budgetSummary; }
    public void setBudgetSummary(DashboardSummaryDto budgetSummary) { this.budgetSummary = budgetSummary; }

    public MonthSummaryDto getMonthAnalytics() { return monthAnalytics; }
    public void setMonthAnalytics(MonthSummaryDto monthAnalytics) { this.monthAnalytics = monthAnalytics; }

    public List<Alert> getLatestAlerts() { return latestAlerts; }
    public void setLatestAlerts(List<Alert> latestAlerts) { this.latestAlerts = latestAlerts; }
}
