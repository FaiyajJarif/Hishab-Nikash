import { motion } from "framer-motion";
import AnomalyBadge from "./AnomalyBadge";

export default function DailyCategoryTable({
  categories,
  anomalies,
  onSelect,
}) {
  return (
    <motion.div
      initial="hidden"
      animate="visible"
      variants={{
        visible: {
          transition: { staggerChildren: 0.05 },
        },
      }}
      className="space-y-2"
    >
      {categories.map((c) => {
        const anomaly = anomalies.find(
          (a) => a.category === c.categoryName
        );

        return (
          <motion.button
            key={c.categoryName}
            variants={{
              hidden: { opacity: 0, y: 8 },
              visible: { opacity: 1, y: 0 },
            }}
            whileHover={{ scale: 1.015 }}
            onClick={() => onSelect(c)}
            className="w-full flex justify-between items-center rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10 hover:bg-white/10 transition"
          >
            <div className="flex items-center gap-2">
              <div className="font-semibold">{c.categoryName}</div>
              {anomaly && <AnomalyBadge />}
            </div>

            <div className="font-bold text-lime-200">
              ৳{c.amount}
            </div>
          </motion.button>
        );
      })}
    </motion.div>
  );
}
