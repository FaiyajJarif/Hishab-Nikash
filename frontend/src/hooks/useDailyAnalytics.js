import { useEffect, useState } from "react";
import { analyticsApi } from "../api/analyticsApi";

export function useDailyAnalytics(date) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const iso = date.toISOString().slice(0, 10);

    async function load() {
      try {
        setLoading(true);

        const [
          summary,
          categories,
          anomalies,
          alert,
          insight,
        ] = await Promise.all([
          analyticsApi.dailySummary(iso),
          analyticsApi.dailyCategories(iso),
          analyticsApi.dailyAnomalies(iso),
          analyticsApi.dailyAlert(iso),
          analyticsApi.dailyInsight(iso),
        ]);

        setData({
          summary: summary.data,
          categories: categories.data,
          anomalies: anomalies.data,
          alert: alert.data,
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
