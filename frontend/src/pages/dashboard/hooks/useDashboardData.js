import { useEffect, useState, useCallback } from "react";
import { apiRequest } from "../../../lib/api";

export function useDashboardData({ mode, date }) {
  const [data, setData] = useState(null);
  const [groups, setGroups] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const month = date.getMonth() + 1;
      const year = date.getFullYear();

      /* -------- CATEGORIES -------- */
      const catRes = await apiRequest(
        `/api/dashboard/categories?month=${month}&year=${year}`
      );

      const grouped = Object.entries(catRes.data).map(
        ([type, list]) => ({
          title: type,
          items: list.map((c) => ({
            id: c.id,
            name: c.name,
            type,
          
            // category-level
            assigned: Number(c.planned),
            available: Number(c.available),
            spent: c.available < 0 ? Math.abs(Number(c.available)) : 0,
          
            // 🔥 summary-level (DO NOT DROP)
            monthIncome: Number(c.monthIncome),
            totalAssigned: Number(c.assigned),
            goal:
            c.target > 0 || c.totalTargetAmount > 0
              ? {
                  enabled: true,
                  monthlyAmount: Number(c.target ?? 0),
                  type: c.frequency === "TOTAL" ? "TOTAL" : "MONTHLY",
                  totalAmount: Number(c.totalTargetAmount ?? 0),
                  assignedThisMonth: Number(c.planned),
                  assignedAllTime: Number(c.totalAssignedAllTime ?? 0),
                }
              : { enabled: false },
          })),
        })
      );

      const flatCategories = grouped.flatMap((g) => g.items);

      /* -------- TOTALS -------- */
      const first = flatCategories[0] ?? {};

      const income = Number(first.monthIncome ?? 0);
      const assigned = Number(first.totalAssigned ?? 0);

      const totals = {
        income,
        assigned,
        available: income - assigned, // ✅ now works
        spent: flatCategories.reduce((sum, c) => sum + c.spent, 0),
        overspent: flatCategories.reduce(
          (sum, c) => sum + (c.available < 0 ? Math.abs(c.available) : 0),
          0
        ),
      };

      /* -------- SERIES -------- */
      let series = [];

      if (mode === "month") {
        const trend = await apiRequest(
          `/api/analytics/month/trend?month=${month}&year=${year}`
        );

        series =
          trend.data?.weeks?.map((w, i) => ({
            label: `W${i + 1}`,
            income: Number(w.income ?? 0),
            expense: Number(w.expense ?? 0),
          })) ?? [];
      } else {
        const daily = await apiRequest(
          `/api/analytics/daily?date=${date.toISOString().slice(0, 10)}`
        );

        series = [
          {
            label: "Today",
            income: Number(daily.data.income),
            expense: Number(daily.data.expense),
          },
        ];
      }

      setGroups(grouped);
      setData({ categories: flatCategories, totals, series });
    } catch (e) {
      console.error(e);
      setError("Failed to load dashboard data");
    } finally {
      setLoading(false);
    }
  }, [mode, date]);

  // 🔹 initial load
  useEffect(() => {
    load();
  }, [load]);

  // 🔹 refresh listener (THIS FIXES ASSIGN MONEY UI)
  useEffect(() => {
    const refresh = () => load();
    window.addEventListener("dashboard-refresh", refresh);
    return () =>
      window.removeEventListener("dashboard-refresh", refresh);
  }, [load]);

  return { data, groups, loading, error };
}
