export default function PeerComparisonCard({ data }) {
    const ageRange = data?.ageRange || "—";
    const your = Number(data?.yourMonthlySpending ?? 0);
    const peer = Number(data?.peerAverageSpending ?? 0);
    const diff = Number(data?.differencePercentage ?? 0);
  
    const status = diffBadge(diff);
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
        <div className="flex items-center justify-between">
          <div className="text-sm font-semibold text-white/85">
            🧑‍🤝‍🧑 Peer Comparison
          </div>
  
          <span className={`text-xs px-2 py-1 rounded-full ring-1 ${status.cls}`}>
            {status.text}
          </span>
        </div>
  
        <div className="text-xs text-white/60">
          Based on age range: <span className="text-white/80">{ageRange}</span>
        </div>
  
        <div className="space-y-2">
          <Row label="Your monthly spending" value={money(your)} />
          <Row label="Peer average spending" value={money(peer)} />
          <Row
            label="Difference"
            value={`${diff >= 0 ? "+" : ""}${diff.toFixed(1)}%`}
            valueClass={diff >= 0 ? "text-red-300" : "text-lime-300"}
          />
        </div>
  
        <div className="text-xs text-white/55">
          This is a motivational benchmark — not financial advice.
        </div>
      </div>
    );
  }
  
  function Row({ label, value, valueClass = "" }) {
    return (
      <div className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
        <div className="text-sm text-white/70">{label}</div>
        <div className={`text-sm font-semibold ${valueClass}`}>{value}</div>
      </div>
    );
  }
  
  function money(n) {
    return `৳${Number(n || 0).toLocaleString()}`;
  }
  
  function diffBadge(diff) {
    if (Math.abs(diff) <= 5) {
      return {
        text: "Balanced",
        cls: "bg-blue-500/15 text-blue-300 ring-blue-400/20",
      };
    }
    if (diff > 5) {
      return {
        text: "Above peers",
        cls: "bg-red-500/15 text-red-300 ring-red-400/20",
      };
    }
    return {
      text: "Below peers",
      cls: "bg-lime-500/15 text-lime-300 ring-lime-400/20",
    };
  }
  