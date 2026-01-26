export default function BudgetSnapshot({ summary, month, year }) {
    const income = Number(summary?.income ?? 0);
    const assigned = Number(summary?.assigned ?? 0);
    const remaining = Number(summary?.remaining ?? 0);
  
    const badge = healthBadge(remaining);
  
    const info = {
      Income: "Total money available to plan this month.",
      Assigned: "How much you planned across all categories.",
      Remaining: "Income - Assigned. Aim for ≈ 0 for a balanced plan.",
    };
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-5">
        <div className="flex items-center justify-between gap-3 flex-wrap">
          <div>
            <div className="text-lg font-semibold">📌 Budget Snapshot</div>
            <div className="text-sm text-white/60">
              Month: {month}/{year}
            </div>
          </div>
  
          <span
            className={`text-xs px-3 py-1 rounded-full ring-1 ${badge.cls}`}
            title={badge.tip}
          >
            {badge.text}
          </span>
        </div>
  
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Stat
            label="Income"
            value={money(income)}
            hint={info.Income}
          />
          <Stat
            label="Assigned"
            value={money(assigned)}
            hint={info.Assigned}
          />
          <Stat
            label="Remaining"
            value={money(remaining)}
            hint={info.Remaining}
            valueClass={remaining < 0 ? "text-red-300" : "text-lime-300"}
          />
        </div>
  
        <div className="text-xs text-white/55">
          Tip: set income first, then add categories, then assign amounts until Remaining is ~0.
        </div>
      </div>
    );
  }
  
  /* ---------- components ---------- */
  
  function Stat({ label, value, hint, valueClass = "" }) {
    return (
      <div className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10 relative">
        <div className="flex items-center justify-between">
          <div className="text-xs uppercase tracking-wide text-white/60">
            {label}
          </div>
          <InfoDot hint={hint} />
        </div>
        <div className={`mt-2 text-lg font-bold ${valueClass}`}>{value}</div>
      </div>
    );
  }
  
  function InfoDot({ hint }) {
    return (
      <div className="group relative">
        <div className="h-6 w-6 rounded-full bg-lime-500/20 text-lime-300 flex items-center justify-center text-xs font-bold ring-1 ring-lime-400/20 cursor-help">
          i
        </div>
  
        <div className="pointer-events-none opacity-0 group-hover:opacity-100 transition absolute right-0 top-7 z-50 w-56 rounded-xl bg-black/80 p-3 text-xs text-white/80 ring-1 ring-white/15">
          {hint}
        </div>
      </div>
    );
  }
  
  /* ---------- helpers ---------- */
  
  function money(n) {
    const x = Number(n || 0);
    return `৳${x.toLocaleString()}`;
  }
  
  function healthBadge(remaining) {
    const r = Number(remaining || 0);
    if (Math.abs(r) <= 5) {
      return {
        text: "🟢 Balanced",
        cls: "bg-lime-500/15 text-lime-300 ring-lime-400/20",
        tip: "Remaining is close to 0 — your plan is balanced.",
      };
    }
    if (r > 5) {
      return {
        text: "🟡 Under-allocated",
        cls: "bg-yellow-500/15 text-yellow-300 ring-yellow-400/20",
        tip: "You still have money unassigned. Allocate it to categories.",
      };
    }
    return {
      text: "🔴 Over-allocated",
      cls: "bg-red-500/15 text-red-300 ring-red-400/20",
      tip: "You assigned more than your income. Reduce category plans.",
    };
  }
  