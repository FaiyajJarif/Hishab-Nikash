import { useEffect, useState } from "react";
import { recurringBillApi } from "../api/recurringBillApi";

export default function RecurringBillsCard({ categories }) {
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    const res = await recurringBillApi.list();
    setBills(res?.data ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, []);

  async function toggle(id) {
    await recurringBillApi.toggle(id);
    load();
  }

  async function remove(id) {
    if (!confirm("Delete this recurring bill?")) return;
    await recurringBillApi.remove(id);
    load();
  }

  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
      <div className="flex justify-between items-center">
        <div>
          <div className="text-lg font-extrabold">🔁 Recurring Bills</div>
          <div className="text-xs text-white/60">
            Auto-paid on due date
          </div>
        </div>
      </div>

      {loading && <div className="text-sm text-white/60">Loading…</div>}

      {!loading && bills.length === 0 && (
        <div className="text-sm text-white/60 mt-3">
          No recurring bills yet.
        </div>
      )}

      <div className="mt-4 space-y-2">
        {bills.map(b => (
          <div
            key={b.id}
            className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
          >
            <div>
              <div className="text-sm font-semibold">
                {b.name}
                {!b.active && (
                  <span className="ml-2 text-xs text-red-300">(Paused)</span>
                )}
              </div>
              <div className="text-xs text-white/60">
                ৳{b.amount} · {b.frequency} · next {b.nextDueDate}
              </div>
            </div>

            <div className="flex gap-2">
              <button
                onClick={() => toggle(b.id)}
                className="rounded-lg bg-white/10 px-3 py-1 text-xs hover:bg-white/20"
              >
                {b.active ? "Pause" : "Resume"}
              </button>
              <button
                onClick={() => remove(b.id)}
                className="rounded-lg bg-red-500/20 px-3 py-1 text-xs text-red-300"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
