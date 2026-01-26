import { motion } from "framer-motion";

export default function SpendingPaceBar({ summary, insight }) {
  if (!summary || !insight) return null;

  const expected = Number(insight.avg ?? 0);
  const actual = Number(summary.expense ?? 0);

  const ratio = expected === 0 ? 0 : actual / expected;
  const pct = Math.min(100, Math.round(ratio * 100));

  let bar = "bg-lime-300";
  if (ratio > 1) bar = "bg-red-400";
  else if (ratio > 0.8) bar = "bg-orange-300";

  return (
    <motion.div
      layout
      whileHover={{ y: -2 }}
      className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15 hover:ring-lime-300/40 transition shadow-[0_20px_60px_rgba(0,0,0,0.25)]"
    >
      <div className="mb-2 text-sm font-semibold">Spending Pace</div>
      <div className="text-xs text-white/60 mb-2">
        Today vs expected daily average
      </div>

      <div className="h-3 rounded-full bg-white/10 overflow-hidden">
        <div
          className={`h-full ${bar} transition-all`}
          style={{ width: `${pct}%` }}
        />
      </div>

      <div className="mt-2 text-xs text-white/70">
        ৳{actual.toLocaleString()} of ৳{expected.toLocaleString()} expected
      </div>
    </motion.div>
  );
}
