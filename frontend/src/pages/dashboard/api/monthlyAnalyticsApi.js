import { apiRequest } from "../../../lib/api";

export const monthlyAnalyticsApi = {
  async summary(month, year) {
    const res = await apiRequest(
      `/api/analytics/month/summary?month=${month}&year=${year}`
    );
    return res.data;
  },

  async categories(month, year) {
    const res = await apiRequest(
      `/api/analytics/month/categories?month=${month}&year=${year}`
    );
    return res.data;
  },

  async trend(month, year) {
    const res = await apiRequest(
      `/api/analytics/month/trend?month=${month}&year=${year}`
    );
    return res.data;
  },

  async overspend(month, year) {
    const res = await apiRequest(
      `/api/analytics/alerts/category?month=${month}&year=${year}`
    );
    return res.data;
  },

  async closeMonth(month, year) {
    const res = await apiRequest(
      `/api/analytics/month/close?month=${month}&year=${year}`,
      { method: "POST" }
    );
    return res.data;
  },

  async reopenMonth(month, year) {
    const res = await apiRequest(
      `/api/analytics/month/reopen?month=${month}&year=${year}`,
      { method: "POST" }
    );
    return res.data;
  },
  
  async calendar(month, year) {
    const res = await apiRequest(
      `/api/analytics/calendar?month=${month}&year=${year}`
    );
    return res.data;
  },

  async history() {
    const res = await apiRequest("/api/analytics/month/history");
    return res.data;
  },
  async rolling(days, endDate) {
    const res = await apiRequest(
      `/api/analytics/rolling?days=${days}&endDate=${endDate}`
    );
    return res.data;
  },
};
