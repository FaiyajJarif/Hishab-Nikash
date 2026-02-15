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

      /* ✅ 1. FETCH CATEGORIES */
      const catRes = await apiRequest(
        `/api/dashboard/categories?month=${month}&year=${year}`
      );

      /* ✅ 2. FETCH TRANSACTIONS */
      const txRes = await apiRequest(`/api/transactions/recent?limit=200`);

      const transactions = txRes.data ?? [];

      /* ✅ 3. BUILD SPENT MAP (categoryId → total spent) */
      const spentMap = {};

      transactions.forEach((tx) => {
        if (tx.type !== "EXPENSE") return;

        spentMap[tx.categoryId] =
          (spentMap[tx.categoryId] ?? 0) + Number(tx.amount);
      });

      /* ✅ 4. BUILD CATEGORY GROUPS WITH REAL SPENT */
      const grouped = Object.entries(catRes.data).map(([type, list]) => ({
        title: type,
        items: list.map((c) => {
          const spent = spentMap[c.id] ?? 0;

          return {
            id: c.id,
            name: c.name,
            type,

            assigned: Number(c.planned),
            available: Number(c.available),

            /* ✅ REAL SPENT */
            spent,

            overspent:
              Number(c.available) < 0
                ? Math.abs(Number(c.available))
                : 0,

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
          };
        }),
      }));

      const flatCategories = grouped.flatMap((g) => g.items);

      /* ✅ TOTALS */
      const first = flatCategories[0] ?? {};
      const income = Number(first.monthIncome ?? 0);
      const assigned = Number(first.totalAssigned ?? 0);

      const totals = {
        income,
        assigned,
        available: income - assigned,

        /* ✅ TOTAL SPENT */
        spent: flatCategories.reduce((sum, c) => sum + c.spent, 0),

        overspent: flatCategories.reduce(
          (sum, c) => sum + c.overspent,
          0
        ),
      };

      setGroups(grouped);
      setData({ categories: flatCategories, totals });
    } catch (e) {
      console.error(e);
      setError("Failed to load dashboard data");
    } finally {
      setLoading(false);
    }
  }, [mode, date]);

  /* ✅ INITIAL LOAD */
  useEffect(() => {
    load();
  }, [load]);

  /* ✅ REFRESH EVENT */
  useEffect(() => {
    const refresh = () => load();
    window.addEventListener("dashboard-refresh", refresh);
    return () =>
      window.removeEventListener("dashboard-refresh", refresh);
  }, [load]);

  return { data, groups, loading, error, reload: load };
}
