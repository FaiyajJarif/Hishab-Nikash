export default function MonthlySummaryCards({ summary }) {
    if (!summary) return null;
  
    return (
      <div className="grid grid-cols-3 lg:grid-cols-6 gap-4">
        <Stat label="Income" value={summary.income} />
        <Stat label="Expense" value={summary.expense} />
        <Stat label="Net" value={summary.net} />
        <Stat label="Assigned" value={summary.assigned} />
        <Stat label="Remaining" value={summary.remaining} />
        <Stat label="Unassigned" value={summary.unassigned} />
      </div>
    );
  }
  
  function Stat({ label, value }) {
    return (
      <div className="rounded-2xl bg-white/10 p-4 ring-1 ring-white/15">
        <div className="text-xs text-white/60">{label}</div>
        <div
          className={[
            "mt-1 text-lg font-bold",
            value < 0 ? "text-red-300" : "text-lime-300",
          ].join(" ")}
        >
          ৳{value?.toLocaleString() ?? "—"}
        </div>
      </div>
    );
  }
  