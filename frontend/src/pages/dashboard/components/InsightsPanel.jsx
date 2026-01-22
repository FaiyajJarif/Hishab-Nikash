import { motion } from "framer-motion";
import BarChartBase from "../charts/BarChartBase";

export default function InsightsPanel({ totals, mode }) {
  const mockBars = [
    { label: "Groceries", value: 320 },
    { label: "Rent", value: 850 },
    { label: "Dining", value: 140 },
    { label: "Transport", value: 95 },
  ];

  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl bg-white/10 backdrop-blur-xl p-6 ring-1 ring-white/15 shadow-[0_20px_60px_rgba(0,0,0,0.25)]"
    >
      <div className="flex items-start justify-between gap-3">
        <div>
          <div className="text-lg font-extrabold tracking-tight">Insights</div>
          <div className="mt-1 text-xs text-white/60">
            {mode === "month" ? "Monthly highlights" : "Daily highlights"}
          </div>
        </div>
        <div className="rounded-2xl bg-white/10 px-3 py-2 text-xs ring-1 ring-white/10 text-white/70">
          Overspent: <span className="text-red-200 font-semibold">{money(totals.overspent)}</span>
        </div>
      </div>

      <div className="mt-4 h-[190px]">
        <BarChartBase data={mockBars} />
      </div>

      <div className="mt-4 space-y-2 text-sm text-white/70">
        <div>• Add “Daily analytics” button → opens a daily analytics page/modal.</div>
        <div>• At month end: show popup with totals + trend comparisons.</div>
        <div>• Manual month close button can live near the date selector.</div>
      </div>
    </motion.div>
  );
}

function money(n) {
  return `৳${Math.round(n).toLocaleString()}`;
}
