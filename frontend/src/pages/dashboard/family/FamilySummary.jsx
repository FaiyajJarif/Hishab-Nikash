import React from "react";
import { motion } from "framer-motion";

function budgetHealth(remaining) {
  if (Math.abs(remaining) <= 50)
    return { label: "Balanced", color: "lime" };
  if (remaining > 0)
    return { label: "Under-allocated", color: "yellow" };
  return { label: "Over-allocated", color: "red" };
}

export default function FamilySummary({
  summary,
  month,
  year,
  canEdit,
  onSaveIncome,
}) {
  if (!summary) {
    return <div className="text-white/60">Loading family budget…</div>;
  }

  const [income, setIncome] = React.useState(summary.income ?? 0);
  const now = new Date();
  const currentYM = now.getFullYear() * 12 + now.getMonth();
  const selectedYM = year * 12 + (month - 1);
  const disabled = selectedYM > currentYM;

  // keep income in sync when summary updates
  React.useEffect(() => {
    setIncome(summary.income ?? 0);
  }, [summary.income]);

  const remaining = Number(summary.remaining ?? 0);
  const assigned = Number(summary.assigned ?? 0);

  async function saveIncome() {
    await onSaveIncome(Number(income));
  }
  const health = budgetHealth(remaining);

  return (
    <motion.div
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-5"
    >
      {/* Header */}
      <div className="text-lg font-semibold">
        📅 {monthName(month)} {year}
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <Stat
        label="Income"
        tip="Total money available for this month."
      >
          <div className="flex gap-2">
            <input
              type="number"
              min="0"
              value={income}
              disabled={!canEdit || disabled}
              onChange={(e) => setIncome(e.target.value)}
              className="w-full rounded-xl bg-black/30 px-3 py-2 text-sm
                         ring-1 ring-white/10 disabled:opacity-50"
            />
            {disabled && (
              <div className="text-xs text-yellow-300 mt-2">
                🔒 Future month — income editing disabled.
              </div>
            )}
            <button
              onClick={saveIncome}
              disabled={!canEdit || disabled}
              className="rounded-xl bg-lime-400 px-3 py-2 text-sm font-semibold
                         disabled:opacity-40"
            >
              Save
            </button>
          </div>

          {!canEdit && (
            <div className="text-xs text-white/50 mt-2">
              Read-only access — ask an admin to edit the budget.
            </div>
          )}
        </Stat>

        <Stat label="Assigned"
        tip="Total amount planned across all categories.">
          <div className="text-lg font-bold">
            ৳{assigned.toLocaleString()}
          </div>
        </Stat>

        <Stat label="Remaining"
        tip="Income minus assigned amounts. Aim for zero."
        >
          <div
            className={`text-lg font-bold ${
              remaining < 0 ? "text-red-300" : "text-lime-300"
            }`}
          >
            ৳{remaining.toLocaleString()}
          </div>
          <div
  className={`mt-2 inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold
    ${
      health.color === "lime"
        ? "bg-lime-500/20 text-lime-300"
        : health.color === "yellow"
        ? "bg-yellow-500/20 text-yellow-300"
        : "bg-red-500/20 text-red-300"
    }`}
>
  {health.label}
</div>
        </Stat>

      </div>
    </motion.div>
  );
}

function Stat({ label, tip, children }) {
  return (
    <div className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10">
      <div className="mb-1 flex items-center gap-1 text-xs uppercase tracking-wide text-white/60">
        <span>{label}</span>
        {tip && <InfoTip text={tip} />}
      </div>
      {children}
    </div>
  );
}


function monthName(m) {
  return new Date(2024, m - 1).toLocaleString("default", { month: "long" });
}

function InfoTip({ text }) {
  const [open, setOpen] = React.useState(false);

  return (
    <div
      className="relative inline-block"
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
    >
      <button
        type="button"
        onClick={() => setOpen(o => !o)}
        className="inline-flex h-5 w-5 items-center justify-center
                   rounded-full bg-lime-500/20 text-xs font-semibold
                   text-lime-300 focus:outline-none"
        aria-label="Info"
      >
        i
      </button>

      {open && (
        <div
          className="absolute left-1/2 z-50 mt-2 w-56 -translate-x-1/2
                     rounded-lg bg-black/90 p-3 text-xs text-white
                     shadow-lg"
        >
          {text}
        </div>
      )}
    </div>
  );
}
