import { apiRequest } from "../../../lib/api";

/**
 * DAILY ANALYTICS API
 * Matches Spring Boot controllers exactly
 */

export function getDailySummary(dateISO) {
  return apiRequest(
    `/api/analytics/daily?date=${encodeURIComponent(dateISO)}`
  ).then(res => res.data);
}

export function getDailyCategories(dateISO) {
  return apiRequest(
    `/api/analytics/daily/categories?date=${encodeURIComponent(dateISO)}`
  ).then(res => res.data);
}

export function getDailyAnomalies(dateISO) {
  return apiRequest(
    `/api/analytics/anomalies/daily?date=${encodeURIComponent(dateISO)}`
  );
}

export function getDailyInsight(dateISO) {
  return apiRequest(
    `/api/analytics/insights/daily?date=${encodeURIComponent(dateISO)}`
  ).then(res => res.data);
}

export function getCalendarHeatmap(date) {
  const month = date.getMonth() + 1;
  const year = date.getFullYear();

  return apiRequest(
    `/api/analytics/calendar?month=${month}&year=${year}`
  ).then(res => res.data);
}