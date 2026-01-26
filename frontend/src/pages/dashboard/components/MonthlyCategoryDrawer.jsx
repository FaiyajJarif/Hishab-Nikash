import { useEffect, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { apiRequest } from "../../../lib/api";

export default function MonthlyCategoryDrawer({
  open,
  category,
  month,
  year,
  onClose,
}) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!open || !category) return;

    setLoading(true);

    apiRequest(
      `/api/analytics/category/month?categoryId=${category.categoryId}&month=${month}&year=${year}`
    )
      .then((res) => setTransactions(res.data || []))
      .finally(() => setLoading(false));
  }, [open, category, month, year]);
  
  useEffect(() => {
    if (!open) return;

    function onKeyDown(e) {
      if (e.key === "Escape") {
        onClose();
      }
    }

    document.addEventListener("keydown", onKeyDown);
    return () => document.removeEventListener("keydown", onKeyDown);
  }, [open, onClose]);

  useEffect(() => {
    if (open) {
      document.querySelector("[tabindex='-1']")?.focus();
    }
  }, [open]);  

  const total = transactions.reduce((s, t) => s + t.amount, 0);

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            className="fixed inset-0 z-40 bg-black/60"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />

            <motion.div
            tabIndex={-1}
            className="fixed right-0 top-0 z-50 h-full w-full max-w-md bg-[#061a12] focus:outline-none"
            initial={{ x: 420 }}
            animate={{ x: 0 }}
            exit={{ x: 420 }}
            transition={{ type: "spring", stiffness: 320, damping: 28 }}
            >
            <div className="flex justify-between px-6 py-5 border-b border-white/10">
              <div>
                <div className="text-lg font-extrabold">
                  {category.categoryName}
                </div>
                <div className="text-xs text-white/60">
                  {new Date(year, month - 1).toLocaleString("default", {
                    month: "long",
                    year: "numeric",
                  })}
                </div>
              </div>

              <button onClick={onClose}>✕</button>
            </div>

            <div className="p-6 space-y-4">
              <div className="text-sm">
                Total spent:{" "}
                <span className="font-bold text-lime-300">
                  ৳{total.toLocaleString()}
                </span>
              </div>

              {loading ? (
                <div className="text-white/60">Loading…</div>
              ) : (
                transactions.map((t) => (
                  <div
                    key={t.id}
                    className="rounded-xl bg-white/5 p-3 ring-1 ring-white/10"
                  >
                    <div className="flex justify-between">
                      <span>{t.note || "Transaction"}</span>
                      <span className="font-bold text-lime-300">
                        ৳{t.amount}
                      </span>
                    </div>
                    <div className="text-xs text-white/50">
                      {new Date(t.date).toLocaleDateString()}
                    </div>
                  </div>
                ))
              )}
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
