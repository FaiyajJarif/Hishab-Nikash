import { apiRequest } from "../../../lib/api";

/**
 * IMPORTANT:
 * Update these endpoints to match your Spring Boot controllers.
 * Everything else in the dashboard uses these functions.
 */
export const dashboardApi = {
    getCategories(month, year) {
        return apiRequest(`/api/dashboard/categories?month=${month}&year=${year}`);
      },
    
      setIncome(payload) {
        return apiRequest("/api/budget/income/set", {
          method: "POST",
          body: payload,
        });
      }, 

      addIncome(payload) {
        return apiRequest("/api/budget/income/add", {
          method: "POST",
          body: payload,
        });
      },
    
      assignMoney(payload) {
        return apiRequest("/api/budget/plan", {
          method: "POST",
          body: payload,
        });
      },
      setTarget(payload) {
        return apiRequest("/api/budget/target", {
          method: "POST",
          body: payload,
        });
      },
    
      addTransaction(payload) {
        return apiRequest("/api/transactions/expense", {
          method: "POST",
          body: payload,
        });
      },
    
      listRecurringBills() {
        return apiRequest("/api/recurring-bills");
      },
  // Overview dashboard (totals + series + categories)
  async getOverview({ mode, dateISO }) {
    // Example endpoint: /api/dashboard/overview?mode=month&date=2026-01-19
    return apiRequest(`/api/dashboard/overview?mode=${mode}&date=${encodeURIComponent(dateISO)}`);
  },

  // Grouped categories (Expenses / Goals / etc.)
  async getCategoryGroups({ mode, dateISO }) {
    // Example: /api/categories/groups?mode=month&date=...
    return apiRequest(`/api/categories/groups?mode=${mode}&date=${encodeURIComponent(dateISO)}`);
  },

  async getCategoryDetails({ categoryId, mode, dateISO }) {
    // Example: /api/categories/{id}/details?mode=month&date=...
    return apiRequest(`/api/categories/${categoryId}/details?mode=${mode}&date=${encodeURIComponent(dateISO)}`);
  },

  async getDailyAnalytics({ dateISO }) {
    return apiRequest(`/api/analytics/daily?date=${encodeURIComponent(dateISO)}`);
  },

  async getMonthlyAnalytics({ monthISO }) {
    return apiRequest(`/api/analytics/monthly?month=${encodeURIComponent(monthISO)}`);
  },

  async getFamilyDashboard() {
    return apiRequest(`/api/family/dashboard`);
  },

  async sendFamilyInvite({ email }) {
    return apiRequest(`/api/family/invite`, { method: "POST", body: { email } });
  },

  async acceptInvite({ token }) {
    return apiRequest(`/api/family/accept?token=${encodeURIComponent(token)}`);
  },
};
