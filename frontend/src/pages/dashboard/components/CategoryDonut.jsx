import { motion } from "framer-motion";
import DonutChartBase from "../charts/DonutChartBase";

export default function CategoryDonut({ categories, onInfo }) {
  const donutData = categories.map((c) => ({ name: c.name, value: c.spent }));

  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl bg-white/10 backdrop-blur-xl p-6 ring-1 ring-white/15 shadow-[0_20px_60px_rgba(0,0,0,0.25)]"
    >
      <div className="flex items-center justify-between">
        <div>
          <div className="text-lg font-extrabold tracking-tight">Category split</div>
          <div className="mt-1 text-xs text-white/60">Where money is going</div>
        </div>
        <div className="text-xs text-white/60">Tap ℹ️ in list</div>
      </div>

      <div className="mt-4 h-[220px]">
        <DonutChartBase data={donutData} />
      </div>

      <div className="mt-4 space-y-2">
        {categories.slice(0, 4).map((c) => (
          <div key={c.id} className="flex items-center justify-between rounded-2xl bg-white/5 px-3 py-2 ring-1 ring-white/10">
            <div className="text-sm text-white/80">{c.name}</div>
            <div className="flex items-center gap-2">
              <div className="text-xs text-white/60">{money(c.spent)}</div>
              <button
                onClick={() => onInfo?.(c)}
                className="grid h-7 w-7 place-items-center rounded-xl bg-white/10 ring-1 ring-white/10 hover:bg-white/15 transition"
                title="Category details"
              >
                ℹ️
              </button>
            </div>
          </div>
        ))}
      </div>
    </motion.div>
  );
}

function money(n) {
  return `৳${Math.round(n).toLocaleString()}`;
}
