import { useState } from "react";
import { motion } from "framer-motion";
import { useDailyAnalytics } from "../hooks/useDailyAnalytics";

import SummaryCards from "../components/SummaryCards";
import CategoryDonut from "../components/CategoryDonut";
import InsightsPanel from "../components/InsightsPanel";
import SpendingLineChart from "../components/SpendingLineChart";

export default function DailyAnalytics() {
  const [date, setDate] = useState(new Date());
  const { data, loading, error } = useDailyAnalytics(date);

  if (loading) return <div className="text-white/70">Loading daily analytics…</div>;
  if (error) return <div className="text-red-200">{error}</div>;
  if (!data) return null;

  const totals = {
    assigned: data.summary.budget,
    spent: data.summary.spent,
    available: data.summary.budget - data.summary.spent,
    overspent: data.overspend?.overspentAmount ?? 0,
  };

  return (
    <div className="space-y-8">
      {/* HEADER */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div>
          <h2 className="text-2xl font-extrabold">
            Daily <span className="text-lime-200">Analytics</span>
          </h2>
          <p className="text-sm text-white/65">
            How today performed vs expectations
          </p>
        </div>

        <DatePill
          date={date}
          onPrev={() => setDate(d => new Date(d.getTime() - 86400000))}
          onNext={() => setDate(d => new Date(d.getTime() + 86400000))}
        />
      </motion.div>

      {/* SUMMARY */}
      <SummaryCards totals={totals} mode="day" />

      {/* CHARTS */}
      <div className="grid gap-6 lg:grid-cols-12">
        <div className="lg:col-span-8">
          <SpendingLineChart
            mode="day"
            series={[
              {
                label: "Today",
                income: data.summary.budget,
                expense: data.summary.spent,
              },
            ]}
          />
        </div>

        <div className="lg:col-span-4 space-y-6">
          <CategoryDonut categories={data.categories} />
          <InsightsPanel
            totals={totals}
            mode="day"
          />
        </div>
      </div>

      {/* ALERTS */}
      {(data.overspend?.overspent || data.anomaly?.isAnomalous) && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="rounded-3xl bg-red-500/10 p-6 ring-1 ring-red-300/20"
        >
          <h3 className="font-extrabold text-red-200">⚠ Attention Needed</h3>

          {data.overspend?.overspent && (
            <p className="mt-2 text-sm text-white/80">
              You overspent by{" "}
              <span className="text-red-200 font-semibold">
                ৳{data.overspend.overspentAmount}
              </span>
            </p>
          )}

          {data.anomaly?.isAnomalous && (
            <p className="mt-2 text-sm text-white/80">
              Unusual spending detected compared to your normal pattern.
            </p>
          )}
        </motion.div>
      )}

      {/* INSIGHT */}
      {data.insight?.message && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15"
        >
          <h3 className="font-semibold text-white/90">💡 Insight</h3>
          <p className="mt-2 text-sm text-white/70">
            {data.insight.message}
          </p>
        </motion.div>
      )}
    </div>
  );
}

function DatePill({ date, onPrev, onNext }) {
  return (
    <div className="flex items-center gap-2 rounded-2xl bg-white/10 px-3 py-2 ring-1 ring-white/15">
      <button onClick={onPrev} className="h-8 w-8 rounded-xl hover:bg-white/10">‹</button>
      <div className="min-w-[160px] text-center text-sm">
        {date.toDateString()}
      </div>
      <button onClick={onNext} className="h-8 w-8 rounded-xl hover:bg-white/10">›</button>
    </div>
  );
}
