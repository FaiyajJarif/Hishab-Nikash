import { useEffect, useMemo, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { apiRequest } from "../../../lib/api";
import CategoryTrendSparkline from "./CategoryTrendSparkline";

export default function CategoryActivityDrawer({
  open,
  category,
  dateISO,
  onClose,
}) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  const title = category?.categoryName ?? category?.name ?? "Category";
  const categoryId = category?.categoryId ?? category?.id ?? null;

  useEffect(() => {
    if (!open || !category) return;

    setLoading(true);

    apiRequest(
      `/api/analytics/daily/category?date=${dateISO}&category=${encodeURIComponent(
        title
      )}`
    )
      .then((res) => {
        // support either {success,data:[...]} OR raw array
        const payload = res?.data?.data ?? res?.data ?? [];
        setTransactions(Array.isArray(payload) ? payload : []);
      })
      .finally(() => setLoading(false));
  }, [open, category, dateISO, title]);

  const total = useMemo(
    () => transactions.reduce((s, t) => s + Number(t.amount ?? 0), 0),
    [transactions]
  );

  return (
    <AnimatePresence>
      {open && (
        <>
          {/* BACKDROP */}
          <motion.div
            className="fixed inset-0 z-40 bg-black/60"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />

          {/* DRAWER */}
          <motion.div
            className="fixed right-0 top-0 z-50 h-full w-full max-w-md bg-[#061a12] ring-1 ring-white/10 shadow-[0_30px_80px_rgba(0,0,0,0.6)]"
            initial={{ x: 420 }}
            animate={{ x: 0 }}
            exit={{ x: 420 }}
            transition={{ type: "spring", stiffness: 320, damping: 28 }}
          >
            {/* HEADER */}
            <div className="flex items-center justify-between border-b border-white/10 px-6 py-5">
              <div>
                <div className="text-lg font-extrabold text-white/95">
                  {title}
                </div>
                <div className="mt-1 text-xs text-white/60">
                  {new Date(dateISO).toDateString()}
                </div>
              </div>

              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.98 }}
                onClick={onClose}
                className="grid h-10 w-10 place-items-center rounded-2xl bg-white/10 ring-1 ring-white/10 hover:bg-white/15 transition"
              >
                ✕
              </motion.button>
            </div>

            {/* CONTENT */}
            <div className="px-6 py-6 space-y-5 overflow-y-auto h-full pb-24">
              <StatRow label="Total spent" value={money(total)} />

              {/* SPARKLINE */}
              <CategoryTrendSparkline categoryId={categoryId} days={14} />

              {/* TRANSACTIONS */}
              <div className="space-y-3">
                <div className="text-xs font-semibold uppercase tracking-wider text-white/60">
                  Transactions
                </div>

                {loading ? (
                  <div className="text-sm text-white/60">Loading…</div>
                ) : transactions.length === 0 ? (
                  <div className="text-sm text-white/50">
                    No transactions on this day
                  </div>
                ) : (
                  transactions.map((t) => (
                    <motion.div
                      key={t.id ?? `${t.date}-${t.amount}-${t.note}`}
                      initial={{ opacity: 0, y: 8 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.2 }}
                      className="rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10 hover:bg-white/10 transition"
                    >
                      <div className="flex justify-between">
                        <div className="font-medium">
                          {t.note || "Transaction"}
                        </div>
                        <div className="font-bold text-lime-300">
                          ৳{Number(t.amount ?? 0).toLocaleString()}
                        </div>
                      </div>
                      <div className="mt-1 text-xs text-white/50">
                        {t.date ? new Date(t.date).toLocaleString() : ""}
                      </div>
                    </motion.div>
                  ))
                )}
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}

function StatRow({ label, value }) {
  return (
    <div className="flex items-center justify-between rounded-3xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
      <div className="text-sm text-white/70">{label}</div>
      <div className="text-sm font-extrabold text-lime-200">{value}</div>
    </div>
  );
}

function money(n) {
  if (n === undefined || n === null) return "—";
  return `৳${Number(n).toLocaleString()}`;
}
