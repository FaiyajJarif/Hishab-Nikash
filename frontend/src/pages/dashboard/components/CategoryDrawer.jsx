import { useEffect, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { dashboardApi } from "../api/dashboardApi";
import TargetModal from "./TargetModal";

export default function CategoryDrawer({ open, category, month, year, onClose }) {
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [targetOpen, setTargetOpen] = useState(false);

  useEffect(() => {
    if (!open || !category?.id) return;

    setLoading(true);
    // you can pass mode/date later; keeping simple:
    dashboardApi
      .getCategoryDetails({ categoryId: category.id, mode: "month", dateISO: new Date().toISOString().slice(0, 10) })
      .then(setDetails)
      .finally(() => setLoading(false));
  }, [open, category]);

  const c = details || category;
  const targetStatus = c ? getTargetStatus(c) : null;

  return (
    <AnimatePresence>
      {open ? (
        <>
          <motion.div
            className="fixed inset-0 z-40 bg-black/60"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />

          <motion.div
            className="fixed right-0 top-0 z-50 h-full w-full max-w-md bg-[#061a12] ring-1 ring-white/10 shadow-[0_30px_80px_rgba(0,0,0,0.55)]"
            initial={{ x: 420 }}
            animate={{ x: 0 }}
            exit={{ x: 420 }}
            transition={{ type: "spring", stiffness: 320, damping: 28 }}
          >
            <div className="flex items-center justify-between border-b border-white/10 px-6 py-5">
              <div>
                <div className="text-lg font-extrabold text-white/95">{c?.name || "Category"}</div>
                <div className="mt-1 text-xs text-white/60">
                  {loading ? "Loading details..." : "Details & activity"}
                </div>
              </div>
              <button
                onClick={onClose}
                className="grid h-10 w-10 place-items-center rounded-2xl bg-white/10 ring-1 ring-white/10 hover:bg-white/15 transition"
              >
                ✕
              </button>
            </div>

            <div className="px-6 py-6 space-y-5">
              <StatRow label="Assigned" value={money(c?.assigned)} />
              <StatRow label="Spent" value={money(c?.spent)} />
              <StatRow
                label="Available"
                value={money(c?.available)}
                emphasis={c?.available < 0 ? "bad" : "good"}
              />

<div className="rounded-3xl bg-white/5 p-4 ring-1 ring-white/10">
  <div className="text-xs font-semibold uppercase tracking-wider text-white/60">
    Goal
  </div>

  <button
    onClick={() => setTargetOpen(true)}
    className="mt-2 w-full rounded-2xl bg-white/10 px-4 py-3"
  >
    🎯 Set target
  </button>

  {c?.goal?.enabled ? (
    <>
      {/* TARGET TEXT */}
      <div className="mt-3 text-sm text-white/80">
        Target:{" "}
        <span className="text-lime-200 font-semibold">
          {c.goal.type === "MONTHLY"
            ? `${money(c.goal.monthlyAmount)} / month`
            : `${money(c.goal.monthlyAmount)} / month until ${money(
                c.goal.totalAmount
              )}`}
        </span>
      </div>

      {/* TOTAL TARGET PROGRESS */}
      {c.goal.type === "TOTAL" &&
        typeof c.goal.assignedAllTime === "number" &&
        typeof c.goal.totalAmount === "number" && (
          <>
            <div className="mt-2 text-xs text-white/70">
              Progress: {money(c.goal.assignedAllTime)} /{" "}
              {money(c.goal.totalAmount)}
            </div>

            <div className="mt-2 h-2 rounded-full bg-white/10 overflow-hidden">
              <div
                className="h-full bg-lime-300 transition-all"
                style={{
                  width: `${Math.min(
                    100,
                    (c.goal.assignedAllTime / c.goal.totalAmount) * 100
                  )}%`,
                }}
              />
            </div>
          </>
        )}

      {/* STATUS */}
      {targetStatus && (
        <div
          className={[
            "mt-3 rounded-xl px-3 py-2 text-sm",
            targetStatus.type === "under"
              ? "bg-red-400/10 text-red-200"
              : "bg-lime-300/10 text-lime-200",
          ].join(" ")}
        >
          🎯 {targetStatus.message}
        </div>
      )}
    </>
  ) : (
    <div className="mt-2 text-sm text-white/70">No goal set.</div>
  )}
</div>

              <div className="rounded-3xl bg-white/5 p-4 ring-1 ring-white/10">
                <div className="text-xs font-semibold uppercase tracking-wider text-white/60">
                  Family activity
                </div>
                <div className="mt-2 flex flex-wrap gap-2">
                  {(c?.family || []).length ? (
                    c.family.map((m) => (
                      <span
                        key={m}
                        className="rounded-full bg-white/10 px-3 py-1 text-xs text-white/80 ring-1 ring-white/10"
                      >
                        {m}
                      </span>
                    ))
                  ) : (
                    <span className="text-sm text-white/65">No family activity found.</span>
                  )}
                </div>
              </div>
            </div>
          </motion.div>
          <TargetModal
            open={targetOpen}
            onClose={() => setTargetOpen(false)}
            category={c}
            month={month}
            year={year}
          />
        </>
      ) : null}
    </AnimatePresence>
  );
}

function StatRow({ label, value, emphasis }) {
  const color =
    emphasis === "bad" ? "text-red-200" : emphasis === "good" ? "text-lime-200" : "text-white/90";

  return (
    <div className="flex items-center justify-between rounded-3xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
      <div className="text-sm text-white/70">{label}</div>
      <div className={["text-sm font-extrabold", color].join(" ")}>{value}</div>
    </div>
  );
}

function money(n) {
  if (n === undefined || n === null) return "—";
  const sign = n < 0 ? "-" : "";
  return `${sign}৳${Math.abs(n).toLocaleString()}`;
}
function getTargetStatus(category) {
  if (!category?.goal?.enabled) return null;

  const g = category.goal;
  const assigned = category.assigned ?? 0;

  // MONTHLY TARGET
  if (g.type === "MONTHLY") {
    if (assigned < g.monthlyAmount) {
      return {
        type: "under",
        message: `Budget ₹${(g.monthlyAmount - assigned).toLocaleString()} more this month`
      };
    }
    return {
      type: "met",
      message: "Monthly target met ✅"
    };
  }

  // TOTAL TARGET
  if (g.type === "TOTAL") {
    return {
      type: "under",
      message: `Budget ₹${g.monthlyAmount.toLocaleString()} this month to reach goal`
    };
  }

  return null;
}

