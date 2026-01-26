export default function TrendCard({ data }) {
    const income = Number(data?.incomeChangePct ?? 0);
    const spend = Number(data?.spendingChangePct ?? 0);
    const save = Number(data?.savingsChangePct ?? 0);
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
        <div className="text-sm font-semibold text-white/85">
          📈 Month-over-month Trend
        </div>
  
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <TrendStat label="Income" pct={income} goodWhen="up" />
          <TrendStat label="Spending" pct={spend} goodWhen="down" />
          <TrendStat label="Savings" pct={save} goodWhen="up" />
        </div>
  
        <div className="text-xs text-white/55">
          Trend compares this month to previous month.
        </div>
      </div>
    );
  }
  
  function TrendStat({ label, pct, goodWhen }) {
    const up = pct >= 0;
    const arrow = up ? "▲" : "▼";
  
    // for spending, down is good; for income/savings, up is good
    const good =
      label === "Spending" ? !up : up;
  
    const cls = good
      ? "text-lime-300 bg-lime-500/10 ring-lime-400/15"
      : "text-red-300 bg-red-500/10 ring-red-400/15";
  
    return (
      <div className={`rounded-2xl p-4 ring-1 ${cls}`}>
        <div className="text-xs uppercase tracking-wide text-white/70">
          {label}
        </div>
        <div className="mt-2 text-lg font-bold">
          {arrow} {Math.abs(pct).toFixed(1)}%
        </div>
        <div className="mt-1 text-xs text-white/60">
          {goodWhen === "up" ? "Higher is better" : "Lower is better"}
        </div>
      </div>
    );
  }
  