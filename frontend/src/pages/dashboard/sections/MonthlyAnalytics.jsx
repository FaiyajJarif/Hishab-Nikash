import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { monthlyAnalyticsApi } from "../api/monthlyAnalyticsApi";
import {
  exportMonthlyPdf,
} from "../utils/exportPdf";
import {
  exportMonthlyCsv
} from "../utils/exportCsv";
import MonthlySummaryCards from "../components/MonthlySummaryCards";
import MonthlyTrendChart from "../components/MonthlyTrendChart";
import MonthlyOverspendAlert from "../components/MonthlyOverspendAlert";
import MonthlyCalendarHeatmap from "../components/MonthlyCalendarHeatmap";
import MonthComparisonChart from "../components/MonthComparisonChart";
import SnapshotHistory from "../components/SnapshotHistory";

export default function MonthlyAnalytics() {
  const [month, setMonth] = useState(1);
  const [year, setYear] = useState(2026);

  const [summary, setSummary] = useState(null);
  const [categories, setCategories] = useState([]);
  const [trend, setTrend] = useState(null);
  const [overspends, setOverspends] = useState([]);
  const [snapshots, setSnapshots] = useState([]);

  const [loading, setLoading] = useState(false);
  const [closing, setClosing] = useState(false);

  const [openCategory, setOpenCategory] = useState(null);
  const [calendar, setCalendar] = useState([]);
  const [rolling, setRolling] = useState([]);

  const [compareA, setCompareA] = useState(null);
  const [compareB, setCompareB] = useState(null);

  // 🔄 Load month analytics
  async function loadMonth() {
    setLoading(true);
    const endDate = `${year}-${String(month).padStart(2, "0")}-28`;

    const [s, c, t, o, cal, hist, roll] = await Promise.all([
      monthlyAnalyticsApi.summary(month, year),
      monthlyAnalyticsApi.categories(month, year),
      monthlyAnalyticsApi.trend(month, year),
      monthlyAnalyticsApi.overspend(month, year),
      monthlyAnalyticsApi.calendar(month, year),
      monthlyAnalyticsApi.history(),
      monthlyAnalyticsApi.rolling(30, endDate), // ✅ ADD
    ]);    

    setSummary(s);
    setCategories(c ?? []);
    setTrend(t);
    setOverspends(o ?? []);
    setLoading(false);
    setCalendar(cal ?? []);
    setSnapshots(hist ?? []);
    setRolling(roll ?? []);
  }

  useEffect(() => {
    loadMonth();
  }, [month, year]);

  // 🔒 Close month
  async function handleCloseMonth() {
    if (!confirm("Close this month? This will lock analytics.")) return;

    try {
      setClosing(true);
      await monthlyAnalyticsApi.closeMonth(month, year);
      await loadMonth();
    } catch (e) {
      alert(e?.response?.data?.error?.message ?? "Failed to close month");
    } finally {
      setClosing(false);
    }
  }

  // 🔓 Reopen month
  async function handleReopenMonth() {
    if (!confirm("Reopen this month? This will unlock analytics.")) return;

    try {
      setClosing(true);
      await monthlyAnalyticsApi.reopenMonth(month, year);   
      await loadMonth();
    } catch (e) {
      alert(e?.response?.data?.error?.message ?? "Failed to reopen month");
    } finally {
      setClosing(false);
    }
  }

  if (loading || !summary) {
    return <div className="text-white/60">Loading monthly analytics…</div>;
  }
  const currentSnapshot = snapshots.find(
    s => s.month === month && s.year === year && s.closedAt
  );
  
  const isLocked = !!currentSnapshot;  
  const isClosedMonth = snapshots.some(
    s => s.month === month &&
         s.year === year &&
         s.closedAt
  );
const insight = getMonthlyInsight(summary, overspends);
const moneyStory = getWhereDidMoneyGo(summary, categories);
const drift = getBudgetDrift(calendar, summary, month, year);
const lastTwo = snapshots.slice(-2);
const momentum =
  lastTwo.length === 2
    ? getMomentum(lastTwo[0], lastTwo[1])
    : null;

return (
  <div className="space-y-10">
    {/* HEADER (do NOT wrap this) */}
    <motion.div
  initial={{ opacity: 0, y: 4 }}
  animate={{ opacity: 1, y: 0 }}
  transition={{ duration: 0.4, ease: "easeOut" }}  
  className="flex items-center justify-between gap-4"
>
  {/* LEFT: title */}
    <div>
      <div className="text-2xl font-extrabold">
        Monthly <span className="text-lime-300">Analytics</span>
      </div>

      <div className="text-sm text-white/60">
        {new Date(year, month - 1).toLocaleString("default", {
          month: "long",
          year: "numeric",
        })}
      </div>

      {/* ◀ ▶ Month navigation */}
      <div className="flex items-center gap-2 mt-2">
        <button
          onClick={() => {
            const d = new Date(year, month - 2);
            setMonth(d.getMonth() + 1);
            setYear(d.getFullYear());
          }}
          className="text-sm px-2 py-1 rounded bg-white/10 hover:bg-white/20"
        >
          ◀
        </button>

        <button
          onClick={() => {
            const d = new Date(year, month);
            setMonth(d.getMonth() + 1);
            setYear(d.getFullYear());
          }}
          className="text-sm px-2 py-1 rounded bg-white/10 hover:bg-white/20"
        >
          ▶
        </button>
      </div>
    </div>

  {/* RIGHT: actions */}
  <div className="flex items-center gap-3">
    <button
      onClick={() => exportMonthlyCsv(summary, categories, snapshots)}
      className="rounded-xl bg-white/10 px-4 py-2 hover:bg-white/20 transition"
    >
      📊 Export CSV
    </button>

    <button
      onClick={() => exportMonthlyPdf(summary, categories, snapshots)}
      className="rounded-xl bg-lime-500/20 px-4 py-2 text-lime-200 hover:bg-lime-500/30 transition"
    >
      📄 Export PDF
    </button>

    {isLocked ? (
      <>
        <span className="text-xs px-3 py-1 rounded-full bg-red-500/20 text-red-300">
         Closed on {new Date(currentSnapshot.closedAt).toLocaleDateString()}
        </span>

        <button
          onClick={handleReopenMonth}
          disabled={closing}
          className="rounded-xl bg-white/10 px-4 py-2 hover:bg-white/20 disabled:opacity-50"
        >
          Reopen month
        </button>
      </>
    ) : (
      <button
        onClick={handleCloseMonth}
        disabled={closing}
        className="rounded-xl bg-red-500/20 px-4 py-2 text-red-300 hover:bg-red-500/30 disabled:opacity-50"
      >
        Close month
      </button>
    )}
  </div>
</motion.div>

    {/* 🔒 LOCK BANNER (outside animation) */}
    {isLocked && (
      <motion.div
      initial={{ opacity: 0, y: 4 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, ease: "easeOut" }}      
        className="rounded-2xl bg-red-500/10 p-4 ring-1 ring-red-300/30"
      >
        🔒 This month was closed on{" "}
        <strong>{new Date(currentSnapshot.closedAt).toLocaleDateString()}</strong>
        Analytics are locked.
      </motion.div>
    )}

    {/* 🔥 EVERYTHING BELOW GETS LOCK EFFECT */}
    <motion.div
      animate={{
        opacity: isLocked ? 0.6 : 1,
        filter: isLocked ? "grayscale(60%)" : "grayscale(0%)",
      }}
      transition={{ duration: 0.4, ease: "easeOut" }}
      className="space-y-10"
    >
      {/* SUMMARY */}
      <MonthlySummaryCards summary={summary} />

      {/* INSIGHT */}
      <motion.div
        initial={{ opacity: 0, y: 4 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, ease: "easeOut" }}        
        className="rounded-3xl bg-white/5 p-5 ring-1 ring-white/10"
      >
        <div className="text-lg font-semibold">
          {insight.icon} Monthly Insight
        </div>
        <div className="mt-2 text-sm text-white/70">
          {insight.message}
        </div>
        {momentum && (
        <div className="mt-3 text-sm font-semibold text-white/80">
          {momentum.icon} Momentum: {momentum.text}
        </div>
      )}
      </motion.div>

      {moneyStory && (
        <motion.div
        initial={{ opacity: 0, y: 4 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, ease: "easeOut" }}        
          className="rounded-2xl bg-white/5 p-5 ring-1 ring-white/10"
        >
          <div className="text-sm font-semibold">
            🧭 Where did the money go?
          </div>
          <div className="mt-2 text-sm text-white/70">
            {moneyStory}
          </div>
        </motion.div>
      )}
      
      {!isLocked && (
        <motion.div
        initial={{ opacity: 0, y: 4 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, ease: "easeOut" }}        
          className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10"
        >
          <div className="text-sm font-semibold mb-2">
            Before closing this month:
          </div>

          <ul className="text-sm text-white/70 space-y-1">
            <li>✔ Review overspent categories</li>
            <li>✔ Confirm all transactions are entered</li>
            <li>✔ Assign remaining income</li>
          </ul>
        </motion.div>
      )}

      {/* 📅 Calendar heatmap */}
        <MonthlyCalendarHeatmap
          days={calendar}
          rolling={rolling} 
          onSelect={(date) => {
            if (isLocked) return;
            navigate(`/analytics/daily?date=${date}`);
          }}
        />

        {drift && (
          <motion.div
          initial={{ opacity: 0, y: 4 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, ease: "easeOut" }}          
            className="rounded-xl bg-orange-500/10 px-4 py-3 ring-1 ring-orange-300/30"
          >
            <div className="text-sm text-orange-200">
              {drift}
            </div>
          </motion.div>
)}
      {/* 🔎 Month comparison hint */}
      {snapshots.length < 2 && (
        <motion.div
        initial={{ opacity: 0, y: 4 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, ease: "easeOut" }}        
          className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10"
        >
          <div className="text-sm font-semibold mb-1">
            📊 Month-to-month comparison
          </div>
          <div className="text-sm text-white/60">
            Close at least <strong>two months</strong> to unlock spending comparisons
            and trend insights between months.
          </div>
        </motion.div>
      )}
      {snapshots.length >= 2 && (
      <div className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10 space-y-3">
        <div className="text-sm font-semibold">
          Compare months manually
        </div>

  <div className="flex gap-3">
    <select
      className="bg-black/30 rounded-lg px-3 py-2 text-sm"
      onChange={e => setCompareA(JSON.parse(e.target.value))}
    >
      <option value="">Select month A</option>
      {snapshots.map(s => (
        <option
          key={`a-${s.year}-${s.month}`}
          value={JSON.stringify(s)}
        >
          {s.month}/{s.year}
        </option>
      ))}
    </select>

    <select
      className="bg-black/30 rounded-lg px-3 py-2 text-sm"
      onChange={e => setCompareB(JSON.parse(e.target.value))}
    >
      <option value="">Select month B</option>
      {snapshots.map(s => (
        <option
          key={`b-${s.year}-${s.month}`}
          value={JSON.stringify(s)}
        >
          {s.month}/{s.year}
        </option>
      ))}
    </select>
  </div>
</div>)}
      {compareA && compareB && (
        <MonthComparisonChart
          snapshots={[compareA, compareB]}
          mode="manual"
        />
      )}

      {snapshots.length >= 2 && (
        <MonthComparisonChart snapshots={snapshots} />
      )}

      {/* TREND */}
      <MonthlyTrendChart trend={trend} />

      {/* CATEGORY BREAKDOWN */}
      <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
        <div className="mb-3 text-sm font-semibold">Category Breakdown</div>

        <div className="space-y-2">
          {categories.map((c, i) => (
            <motion.div
              key={c.categoryId}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.04, duration: 0.7, ease: "easeOut" }}
              onClick={() => {
                if (isLocked) return;
                setOpenCategory(c);
              }}
              className={`
                flex justify-between rounded-xl bg-white/5 px-4 py-3
                ring-1 ring-white/10 transition
                ${
                  isLocked
                    ? "cursor-not-allowed opacity-50"
                    : "cursor-pointer hover:bg-white/10 hover:scale-[1.01]"
                }
                hover:shadow-[0_12px_40px_rgba(0,0,0,0.35)]
                hover:-translate-y-[1px]
              `}
            >
              <span className="font-medium">{c.categoryName}</span>
              <span className="font-bold text-lime-200">
                ৳{c.amount.toLocaleString()}
              </span>
            </motion.div>
          ))}
        </div>
      </div>

      {/* OVERSPENDS */}
      <MonthlyOverspendAlert alerts={overspends} />
    </motion.div>
    {/* SNAPSHOT HISTORY */}
    <SnapshotHistory
      snapshots={snapshots}   // ✅ FIX
      onSelect={(snap) => {
        setMonth(snap.month);
        setYear(snap.year);
      }}
    />

    {/* DRAWER (outside lock wrapper) */}
    {openCategory && (
      <MonthlyCategoryDrawer
        open
        category={openCategory}
        month={month}
        year={year}
        onClose={() => setOpenCategory(null)}
      />
    )}
  </div>
);
}
function getMonthlyInsight(summary, overspends, trend) {
  if (overspends.length > 0) {
    return {
      icon: "🔥",
      message: `A few categories ran higher than planned. This is a good moment to decide where next month’s money should work harder.`,
    };
  }

  if (summary.unassigned < 0) {
    return {
      icon: "⚠",
      message: "Some dollars were assigned before they were earned. Consider rolling this forward intentionally.",
    };
  }

  if (trend?.values?.slice(-1)[0] === 0) {
    return {
      icon: "💡",
      message: "Spending tapered off toward the end of the month — a strong sign of control.",
    };
  }

  return {
    icon: "💡",
    message: "This month stayed balanced. Your plan and spending were well aligned.",
  };
}

function getWhereDidMoneyGo(summary, categories) {
  if (!categories.length || summary.expense === 0) return null;

  const sorted = [...categories].sort((a, b) => b.amount - a.amount);
  const top = sorted[0];
  const pct = Math.round((top.amount / summary.expense) * 100);

  if (pct >= 50) {
    return `Most of your spending went to ${top.categoryName}, taking ${pct}% of the total. This month was heavily concentrated.`;
  }

  if (pct >= 30) {
    return `${top.categoryName} was your largest expense at ${pct}% of total spending, followed by several mid-sized categories.`;
  }

  return `Your spending was distributed across multiple categories with no single dominant expense.`;
}

function getBudgetDrift(calendar, summary, month, year) {
  if (!calendar.length || summary.expense === 0) return null;

  const daysInMonth = new Date(year, month, 0).getDate();
  const midPoint = Math.floor(daysInMonth / 2);

  const earlySpend = calendar
    .filter(d => new Date(d.date).getDate() <= midPoint)
    .reduce((s, d) => s + d.amount, 0);

  const pctEarly = Math.round((earlySpend / summary.expense) * 100);

  if (pctEarly >= 65) {
    return `⚠ ${pctEarly}% of spending happened in the first half of the month. This increases end-of-month risk.`;
  }

  if (pctEarly <= 35) {
    return `💡 Spending was well paced, with most expenses occurring later in the month.`;
  }

  return null;
}
function getMomentum(a, b) {
  if (!a || !b) return null;

  if (b.expense < a.expense && b.remaining > a.remaining) {
    return { icon: "🟢", text: "Improving" };
  }

  if (b.expense > a.expense && b.overspent > a.overspent) {
    return { icon: "🔴", text: "Worsening" };
  }

  return { icon: "🟡", text: "Stable" };
}
