export default function DailySummaryCards({ summary }) {
    if (!summary) return null;
  
    return (
      <div className="grid grid-cols-3 gap-4">
        <Stat label="Spent" value={summary.expense} />
        <Stat label="Transactions" value={summary.transactionCount} />
        <Stat label="Net" value={summary.net} />
      </div>
    );
  }
  
  function Stat({ label, value }) {
    return (
      <div className="rounded-2xl bg-white/10 p-4 ring-1 ring-white/15 hover:bg-white/15 transition">
        <div className="text-xs text-white/60">{label}</div>
        <div className="mt-1 text-lg font-bold text-lime-300">
          {typeof value === "number" ? `৳${value}` : value}
        </div>
      </div>
    );
  }
  