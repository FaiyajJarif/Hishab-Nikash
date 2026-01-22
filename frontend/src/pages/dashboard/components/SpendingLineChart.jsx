import { motion } from "framer-motion";
import LineChartBase from "../charts/LineChartBase";

export default function SpendingLineChart({ series, mode }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl bg-white/10 backdrop-blur-xl p-6 ring-1 ring-white/15 shadow-[0_20px_60px_rgba(0,0,0,0.25)]"
    >
      <div className="flex items-center justify-between gap-3">
        <div>
          <div className="text-lg font-extrabold tracking-tight">Spending vs Income</div>
          <div className="mt-1 text-xs text-white/60">{mode === "month" ? "Trend (last 14 days preview)" : "Today trend preview"}</div>
        </div>
        <div className="rounded-2xl bg-white/10 px-3 py-2 text-xs ring-1 ring-white/10 text-white/70">
          Hover points for values
        </div>
      </div>

      <div className="mt-5 h-[280px]">
        <LineChartBase data={series} />
      </div>
    </motion.div>
  );
}
