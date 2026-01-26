import { motion } from "framer-motion";

export default function MonthlyCalendarHeatmap({
  days,
  rolling = [],
  onSelect,
}) {
  if (!days || days.length === 0) return null;

  /* ---------------------------
   🔢 Log-scaled intensity
   --------------------------- */
    const amounts = days.map(d => d.amount);
    const max = Math.max(...amounts, 1);

    function intensity(amount) {
    if (amount === 0) return "bg-white/5";

    // log scale to spread dense monthly values
    const ratio = Math.log(amount + 1) / Math.log(max + 1);

    if (ratio < 0.35) return "bg-lime-400/30";
    if (ratio < 0.6)  return "bg-lime-400/50";
    if (ratio < 0.8)  return "bg-orange-400/50";
    return "bg-red-400/60";
    }

  /* ---------------------------
     🔥 Rolling average lookup
     --------------------------- */
     const normalize = d => d.slice(0, 10);

     const avgMap = Object.fromEntries(
       rolling.map(r => [normalize(r.date), r.avg])
     );     

  /* ---------------------------
     🧱 UI
     --------------------------- */
  return (
    <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
      <div className="mb-3 text-sm font-semibold">
        Monthly Activity
      </div>

      <div className="grid grid-cols-7 gap-2">
        {days.map((d, i) => {
          const isAboveAvg =
          d.amount > (avgMap[normalize(d.date)] ?? Infinity);        

          return (
            <motion.div
              key={d.date}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: i * 0.01 }}
              title={`${d.date} — ৳${d.amount}`}
              onClick={() => onSelect?.(d.date)}
              className={`
                relative h-8 rounded-lg cursor-pointer
                ${intensity(d.amount)}
                hover:ring-2 hover:ring-lime-300/40
                transition
              `}
            >
              {/* 🔥 Above rolling average */}
              {isAboveAvg && (
                <span className="absolute top-1 right-1 text-xs">
                  🔥
                </span>
              )}
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
