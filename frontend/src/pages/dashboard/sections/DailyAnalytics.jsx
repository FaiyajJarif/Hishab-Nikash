import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { analyticsApi } from "../../../api/analyticsApi";

import DailyCalendarHeatmap from "../components/DailyCalendarHeatmap";
import DailySpendingChart from "../components/DailySpendingChart";
import DailyCategoryTable from "../components/DailyCategoryTable";
import CategoryActivityDrawer from "../components/CategoryActivityDrawer";
import SpendingPaceBar from "../components/SpendingPaceBar";
import { useDailySocket } from "../hooks/useDailySocket";
import InsightCard from "../components/InsightCard";
import DailySpendingTrend from "../components/DailySpendingTrend";


/* ---------------- animations ---------------- */

const fadeUp = {
  initial: { opacity: 0, y: 14 },
  animate: { opacity: 1, y: 0 },
  transition: { duration: 0.35, ease: "easeOut" },
};

const slideVariants = {
  initial: (direction) => ({
    opacity: 0,
    x: direction > 0 ? 40 : -40,
  }),
  animate: {
    opacity: 1,
    x: 0,
  },
  exit: (direction) => ({
    opacity: 0,
    x: direction > 0 ? -40 : 40,
  }),
};

export default function DailyAnalytics() {
  const [date, setDate] = useState(new Date());
  const [direction, setDirection] = useState(0); // -1 = prev, 1 = next

  const [summary, setSummary] = useState(null);
  const [categories, setCategories] = useState([]);
  const [anomalies, setAnomalies] = useState([]);
  const [calendar, setCalendar] = useState([]);
  const [insight, setInsight] = useState(null);
  const [openCategory, setOpenCategory] = useState(null);
  const [rolling, setRolling] = useState(null);
  const iso = date.toISOString().slice(0, 10);
  

  async function loadDaily() {
    const month = date.getMonth() + 1;
    const year = date.getFullYear();
  
    try {
      const results = await Promise.allSettled([
        analyticsApi.dailySummary(iso),
        analyticsApi.dailyCategories(iso),
        analyticsApi.dailyAnomalies(iso),
        analyticsApi.calendarHeatmap(month, year),
        analyticsApi.dailyInsight(iso),
        analyticsApi.rollingAverage(14, iso),
      ]);
  
      const [
        summaryRes,
        categoriesRes,
        anomaliesRes,
        calendarRes,
        insightRes,
        rollingRes,
      ] = results;
  
      /* -------- SUMMARY (critical) -------- */
      const s = summaryRes.status === "fulfilled"
        ? summaryRes.value
        : {
            date: iso,
            expense: 0,
            income: 0,
            net: 0,
            transactionCount: 0,
            topCategory: null,
            spent: 0,
          };
  
      setSummary({
        date: s.date,
        expense: s.expense ?? 0,
        income: s.income ?? 0,
        net: s.net ?? 0,
        transactionCount: s.transactionCount ?? 0,
        topCategory: s.topCategory ?? null,
        spent: s.spent ?? s.expense ?? 0,
      });
  
      /* -------- OPTIONAL SECTIONS -------- */
      setCategories(
        categoriesRes.status === "fulfilled" && Array.isArray(categoriesRes.value)
          ? categoriesRes.value
          : []
      );
  
      setAnomalies(
        anomaliesRes.status === "fulfilled" && Array.isArray(anomaliesRes.value)
          ? anomaliesRes.value
          : []
      );
  
      setCalendar(
        calendarRes.status === "fulfilled" && Array.isArray(calendarRes.value)
          ? calendarRes.value
          : []
      );
  
      setInsight(
        insightRes.status === "fulfilled" ? insightRes.value : null
      );
  
      setRolling(
        rollingRes.status === "fulfilled" ? rollingRes.value : null
      );
  
    } catch (err) {
      console.error("Daily analytics failed hard:", err);
  
      // absolute fallback
      setSummary({
        date: iso,
        expense: 0,
        income: 0,
        net: 0,
        transactionCount: 0,
        topCategory: null,
        spent: 0,
      });
      setCategories([]);
      setAnomalies([]);
      setCalendar([]);
      setInsight(null);
      setRolling(null);
    }
  }  

  useEffect(() => {
    loadDaily();
  }, [iso]);

  useDailySocket({
    dateISO: iso,
    onRefresh: loadDaily,
  });

  if (!summary) {
    return <div className="text-white/60">Loading daily analytics…</div>;
  }

  return (
    <AnimatePresence mode="wait" custom={direction}>
      <motion.div
        key={iso}                     // 🔑 remount on date change
        custom={direction}
        variants={slideVariants}
        initial="initial"
        animate="animate"
        exit="exit"
        transition={{ duration: 0.35, ease: "easeOut" }}
        className="space-y-8"
      >
        {/* HEADER */}
        <motion.div {...fadeUp} className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.97 }}
              onClick={() => {
                setDirection(-1);
                setDate((d) => new Date(new Date(d).setDate(d.getDate() - 1)));
              }}
              className="rounded-lg bg-white/10 px-3 py-2 hover:bg-white/20"
            >
              ←
            </motion.button>

            <div>
              <div className="text-2xl font-extrabold">
                Daily <span className="text-lime-300">Analytics</span>
              </div>
              <div className="text-sm text-white/60">
                {date.toDateString()}
              </div>
            </div>

            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.97 }}
              onClick={() => {
                setDirection(1);
                setDate((d) => new Date(new Date(d).setDate(d.getDate() + 1)));
              }}
              className="rounded-lg bg-white/10 px-3 py-2 hover:bg-white/20"
            >
              →
            </motion.button>
          </div>

          <input
            type="date"
            value={iso}
            onChange={(e) => {
              const newDate = new Date(e.target.value);
              setDirection(newDate > date ? 1 : -1);
              setDate(newDate);
            }}
            className="rounded-xl bg-white/10 px-3 py-2 text-sm ring-1 ring-white/15"
          />
        </motion.div>

        {/* SUMMARY */}
        <motion.div layout {...fadeUp} className="grid grid-cols-3 gap-4">
          <Stat label="Spent" value={summary.spent} />
          <Stat label="Transactions" value={summary.transactionCount} />
          <Stat label="Top Category" value={summary.topCategory || "—"} />
        </motion.div>

        {/* CALENDAR */}
        <motion.div layout {...fadeUp}>
          <DailyCalendarHeatmap days={calendar} onSelect={setDate} />
        </motion.div>

        {/* CHARTS */}
          <motion.div layout {...fadeUp} className="space-y-4">
            <DailySpendingChart summary={summary} insight={insight} />

            {rolling && (
              <DailySpendingTrend
                calendar={calendar}
                rollingSeries={rolling}
              />
            )}
            <SpendingPaceBar summary={summary} insight={insight} />
          </motion.div>

        {/* CATEGORY BREAKDOWN */}
        <motion.div
          layout
          {...fadeUp}
          className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15"
        >
          <div className="mb-3 text-sm font-semibold">
            Category Breakdown
          </div>

          {categories.length === 0 ? (
            <div className="text-sm text-white/50">No spending</div>
          ) : (
            <DailyCategoryTable
              categories={categories}
              anomalies={anomalies}
              onSelect={setOpenCategory}
            />
          )}
        </motion.div>

        {/* INSIGHT */}
        {insight && (
          <motion.div layout {...fadeUp}>
            <InsightCard insight={insight} />
          </motion.div>
        )}

        {/* DRAWER */}
        <AnimatePresence>
          {openCategory && (
            <CategoryActivityDrawer
              open
              category={openCategory}
              dateISO={iso}
              onClose={() => setOpenCategory(null)}
            />
          )}
        </AnimatePresence>
      </motion.div>
    </AnimatePresence>
  );
}

function Stat({ label, value }) {
  return (
    <motion.div
      layout
      className="rounded-2xl bg-white/10 p-4 ring-1 ring-white/15"
    >
      <div className="text-xs text-white/60">{label}</div>
      <div className="mt-1 text-lg font-bold text-lime-300">
        {typeof value === "number" ? `৳${value}` : value}
      </div>
    </motion.div>
  );
}
