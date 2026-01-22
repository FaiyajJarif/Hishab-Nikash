import { useEffect, useState } from "react";
import { apiRequest } from "../../../lib/api";

export function useDailyAnalytics(date) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setError(null);

        const d = date.toISOString().slice(0, 10);

        const [
          summary,
          categories,
          overspend,
          anomaly,
          insight,
        ] = await Promise.all([
          apiRequest(`/api/analytics/daily?date=${d}`),
          apiRequest(`/api/analytics/daily/categories?date=${d}`),
          apiRequest(`/api/analytics/alerts/daily?date=${d}`),
          apiRequest(`/api/analytics/anomaly/day?date=${d}`),
          apiRequest(`/api/analytics/insights/daily?date=${d}`),
        ]);

        setData({
          summary: summary.data,
          categories: categories.data.map((c) => ({
            name: c.categoryName,
            spent: Number(c.amount),
          })),
          overspend: overspend.data,
          anomaly: anomaly,
          insight: insight.data,
        });
      } catch (e) {
        console.error(e);
        setError("Failed to load daily analytics");
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [date]);

  return { data, loading, error };
}