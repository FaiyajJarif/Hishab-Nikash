import { apiRequest } from "../lib/api";

export const analyticsApi = {
  async dailySummary(date) {
    const res = await apiRequest(`/api/analytics/daily?date=${date}`);
    return res.data;
  },

  async dailyCategories(date) {
    const res = await apiRequest(`/api/analytics/daily/categories?date=${date}`);
    return res.data;
  },

  async dailyAnomalies(date) {
    const res = await apiRequest(`/api/analytics/anomalies/daily?date=${date}`);
    return res.data;
  },

  async dailyInsight(date) {
    const res = await apiRequest(`/api/analytics/insights/daily?date=${date}`);
    return res.data;
  },

  async calendarHeatmap(month, year) {
    const res = await apiRequest(
      `/api/analytics/calendar?month=${month}&year=${year}`
    );
    return res.data;
  },

  async rollingAverage(days, dateISO) {
    const res = await apiRequest(
      `/api/analytics/rolling?days=${days}&endDate=${dateISO}`
    );
    return res.data;
  }
  
};
