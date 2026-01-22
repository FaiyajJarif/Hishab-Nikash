import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { dashboardApi } from "../api/dashboardApi";

export default function SetIncomeModal({ open, onClose, date }) {
  const [income, setIncome] = useState("");
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  async function submit() {
    if (!income) {
      setMsg("Enter income amount");
      return;
    }

    setLoading(true);
    setMsg("");

    try {
      await dashboardApi.addIncome({
        month: date.getMonth() + 1,
        year: date.getFullYear(),
        income: Number(income),
      });      

      setMsg("Income saved ✅");

      setTimeout(() => {
        window.dispatchEvent(new Event("dashboard-refresh"));
        onClose();
      }, 500);
    } catch (e) {
      setMsg(e?.message || "Failed to set income");
    } finally {
      setLoading(false);
    }
  }

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            className="fixed inset-0 bg-black/60 z-40"
            onClick={onClose}
          />
          <motion.div
            className="fixed left-1/2 top-1/2 z-50 w-[90vw] max-w-md
                       -translate-x-1/2 -translate-y-1/2 rounded-3xl
                       bg-[#061a12] p-6 ring-1 ring-white/10"
          >
            <h2 className="text-lg font-extrabold">Add income</h2>

            <input
              type="number"
              value={income}
              onChange={(e) => setIncome(e.target.value)}
              placeholder="e.g. 50000"
              className="mt-4 w-full rounded-2xl bg-white/10 px-4 py-3
                         text-white ring-1 ring-white/15"
            />

            {msg && (
              <div className="mt-3 text-sm text-white/80">{msg}</div>
            )}

          <button
            onClick={submit}
            disabled={loading}
            className="mt-4 w-full rounded-2xl bg-lime-300 py-3
                      font-semibold text-[#061a12]"
          >
            {loading ? "Saving..." : "Add income"}
          </button>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
