// src/pages/dashboard/sections/RecurringBillsDashboard.jsx
import { useEffect, useState } from "react";
import { dashboardApi } from "../api/dashboardApi";

export default function RecurringBillsDashboard() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboardApi.listRecurringBills()
      .then(res => setItems(res))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="text-white/70">Loading recurring bills…</div>;
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-extrabold text-white">
        Recurring Bills
      </h1>

      {items.length === 0 && (
        <div className="rounded-2xl bg-white/10 p-6 text-white/70">
          No recurring bills added yet.
        </div>
      )}

      {items.map(b => (
        <div
          key={b.id}
          className="flex items-center justify-between rounded-2xl bg-white/10 p-4 ring-1 ring-white/10"
        >
          <div>
            <div className="font-semibold text-white">{b.name}</div>
            <div className="text-sm text-white/60">
              {b.frequency} • Next: {b.nextDueDate ?? "—"}
            </div>
          </div>

          <div className="text-right">
            <div className="text-lg font-bold text-lime-200">
              ৳{b.amount}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
