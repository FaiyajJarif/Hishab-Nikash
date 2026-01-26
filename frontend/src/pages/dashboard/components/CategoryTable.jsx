import { motion } from "framer-motion";

export default function CategoryTable({ categories, onInfo }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl bg-white/10 backdrop-blur-xl p-6 ring-1 ring-white/15 shadow-[0_20px_60px_rgba(0,0,0,0.25)]"
    >
      <div className="flex items-center justify-between">
        <div>
          <div className="text-lg font-extrabold tracking-tight">Budget categories</div>
          <div className="mt-1 text-xs text-white/60">Assigned • Spent • Available</div>
        </div>
        <button className="rounded-2xl bg-white/10 px-3 py-2 text-xs ring-1 ring-white/10 hover:bg-white/15 transition">
          Manage
        </button>
      </div>

      <div className="mt-5 overflow-hidden rounded-2xl ring-1 ring-white/10">
        <div className="grid grid-cols-12 bg-white/5 px-3 py-2 text-[11px] font-semibold uppercase tracking-wider text-white/60">
          <div className="col-span-5">Category</div>
          <div className="col-span-2 text-right">Assigned</div>
          <div className="col-span-2 text-right">Spent</div>
          <div className="col-span-2 text-right">Avail</div>
          <div className="col-span-1 text-right">Info</div>
        </div>

        <div className="divide-y divide-white/10">
          {categories.map((c) => (
            <div key={c.id} className="grid grid-cols-12 items-center px-3 py-3 text-sm">
              <div className="col-span-5">
              <div className="font-semibold text-white/90 flex items-center gap-2">
                {c.name}

                {isUnderfunded(c) && (
                  <span className="rounded-full bg-red-400/15 px-2 py-0.5 text-[10px] font-bold text-red-300">
                    Underfunded
                  </span>
                )}
              </div>
                {c.goal?.enabled ? (
                  <div className="mt-0.5 text-xs text-lime-200/80">Goal: {c.goal.type === "MONTHLY"
                    ? `${money(c.goal.monthlyAmount)} / month`
                    : `${money(c.goal.monthlyAmount)} / month until ${money(c.goal.totalAmount)}`}</div>
                ) : (
                  <div className="mt-0.5 text-xs text-white/50">No goal</div>
                )}
              </div>

              <div className="col-span-2 text-right text-white/75">{money(c.assigned)}</div>
              <div className="col-span-2 text-right text-white/75">{money(c.spent)}</div>
              <div className={["col-span-2 text-right font-semibold", c.available < 0 ? "text-red-200" : "text-lime-200"].join(" ")}>
                {money(c.available)}
              </div>

              <div className="col-span-1 flex justify-end">
                <button
                  onClick={() => onInfo?.(c)}
                  className="grid h-8 w-8 place-items-center rounded-xl bg-white/10 ring-1 ring-white/10 hover:bg-white/15 transition"
                  title="Category details"
                >
                  ℹ️
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* quick actions */}
      <div className="mt-5 grid gap-3 sm:grid-cols-2">
        <button className="rounded-2xl bg-lime-300 px-4 py-3 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition">
          + Assign money
        </button>
        <button className="rounded-2xl bg-white/10 px-4 py-3 text-sm ring-1 ring-white/15 hover:bg-white/15 transition">
          View transactions
        </button>
      </div>
    </motion.div>
  );
}

function money(n) {
  const sign = n < 0 ? "-" : "";
  const v = Math.abs(n);
  return `${sign}৳${v.toLocaleString()}`;
}
function getUnderfunded(category) {
  if (!category.goal?.enabled) return null;
  if (category.goal.type !== "MONTHLY") return null;

  const diff = category.goal.monthlyAmount - category.assigned;
  return diff > 0 ? diff : null;
}

function isUnderfunded(c) {
  if (!c.goal?.enabled) return false;

  if (c.goal.type === "MONTHLY") {
    return c.assigned < c.goal.monthlyAmount;
  }

  if (c.goal.type === "TOTAL") {
    return c.goal.assignedAllTime < c.goal.totalAmount;
  }

  return false;
}
