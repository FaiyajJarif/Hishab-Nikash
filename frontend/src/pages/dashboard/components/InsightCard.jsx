import { motion } from "framer-motion";

export default function InsightCard({ insight }) {
  if (!insight) return null;

  const { today, avg, diff, message } = insight;

  const ratio = avg === 0 ? 0 : today / avg;

  let badge = "💡";
  let color = "bg-lime-400/15 text-lime-200";

  if (ratio >= 1.25) {
    badge = "🔥";
    color = "bg-red-400/15 text-red-200";
  } else if (ratio > 1) {
    badge = "⚠";
    color = "bg-orange-400/15 text-orange-200";
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className={`rounded-3xl p-5 ring-1 ring-white/15 ${color}`}
    >
      <div className="flex items-center gap-2 font-semibold">
        <span className="text-xl">{badge}</span>
        Daily Insight
      </div>

      <p className="mt-2 text-sm text-white/80">{message}</p>

      <div className="mt-3 text-xs text-white/60">
        Today: ৳{today} · Avg: ৳{avg}
      </div>
    </motion.div>
  );
}

function MiniStat({ label, value }) {
  return (
    <div className="rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
      <div className="text-xs text-white/60">{label}</div>
      <div className="mt-1 text-sm font-bold text-white/90">{value}</div>
    </div>
  );
}
