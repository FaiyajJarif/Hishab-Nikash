import { motion } from "framer-motion";

export default function CategoryGroup({ title, categories, onInfo, onAssign, onView }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15"
    >
      <div className="flex items-center justify-between mb-4">
        <div className="text-lg font-extrabold">{title}</div>

        <div className="flex gap-2">
        <button
            onClick={onAssign}
            disabled={!onAssign}
            className="rounded-xl bg-lime-300 px-4 py-2 text-sm font-semibold text-[#061a12]
                        disabled:opacity-50 disabled:cursor-not-allowed"
            >
            + Assign money
            </button>

            <button
            onClick={onView}
            disabled={!onView}
            className="rounded-xl bg-white/10 px-4 py-2 text-sm ring-1 ring-white/15
                        disabled:opacity-50 disabled:cursor-not-allowed"
            >
            View transactions
            </button>
        </div>
      </div>

      <div className="space-y-2">
        {categories.map((c, idx) => (
          <div
            key={c.id ?? `${title}-${idx}`}
            className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
          >
            <div>
              <div className="font-semibold">{c.name}</div>
              <div className="text-xs text-white/60">
                Assigned ₹{c.assigned} · Spent ₹{c.spent}
              </div>
            </div>

            <button
              onClick={() => onInfo(c)}
              className="h-8 w-8 rounded-full bg-white/10 grid place-items-center"
            >
              <div className="h-5 w-5 rounded-full bg-lime-300 text-[#061a12] text-xs grid place-items-center cursor-pointer">
                    i
                    </div>
            </button>
          </div>
        ))}
      </div>
    </motion.div>
  );
}
