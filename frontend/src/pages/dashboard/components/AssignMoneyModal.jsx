import { useMemo, useState, useEffect } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { dashboardApi } from "../api/dashboardApi";

export default function AssignMoneyModal({
  open,
  onClose,
  categories,
  date,
  remaining,
}) {
  const [categoryId, setCategoryId] = useState("");
  const [amount, setAmount] = useState("");
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const options = useMemo(() => categories || [], [categories]);

  // ✅ Always-safe remaining value
  const safeRemaining =
    typeof remaining === "number" && !Number.isNaN(remaining)
      ? remaining
      : 0;

  // reset state when modal opens
  useEffect(() => {
    if (open) {
      setCategoryId("");
      setAmount("");
      setNote("");
      setMsg("");
    }
  }, [open]);

  async function submit() {
    setMsg("");

    if (!categoryId) {
      setMsg("Please select a category.");
      return;
    }

    const numericAmount = Number(amount);

    if (!numericAmount || numericAmount <= 0) {
      setMsg("Enter a valid amount greater than 0.");
      return;
    }

    if (!date) {
      setMsg("Date not available.");
      return;
    }

    // ✅ FRONTEND GUARD (matches backend rule)
    if (numericAmount > safeRemaining) {
      setMsg("Not enough money available to assign.");
      return;
    }

    setLoading(true);
    try {
      await dashboardApi.assignMoney({
        month: date.getMonth() + 1,
        year: date.getFullYear(),
        categoryId: Number(categoryId),
        amount: numericAmount,
        note,
      });

      setMsg("Assigned successfully ✅");

      setTimeout(() => {
        onClose?.();
        window.dispatchEvent(new Event("dashboard-refresh"));
      }, 600);
    } catch (e) {
      console.error(e);
      setMsg(
        e?.error?.message ||
          e?.message ||
          "Assignment failed. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <AnimatePresence>
      {open && (
        <>
          {/* backdrop */}
          <motion.div
            className="fixed inset-0 z-40 bg-black/60"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />

          {/* modal */}
          <motion.div
            className="fixed left-1/2 top-1/2 z-50 w-[92vw] max-w-lg
                       -translate-x-1/2 -translate-y-1/2
                       rounded-3xl bg-[#061a12] p-6
                       ring-1 ring-white/10 shadow-[0_30px_80px_rgba(0,0,0,0.55)]"
            initial={{ opacity: 0, scale: 0.95, y: 10 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.97, y: 10 }}
          >
            {/* header */}
            <div className="flex items-center justify-between">
              <div>
                <div className="text-lg font-extrabold">Assign money</div>
                <div className="mt-1 text-xs text-white/60">
                  Update assigned amount for this month
                </div>
              </div>
              <button
                onClick={onClose}
                className="grid h-10 w-10 place-items-center rounded-2xl
                           bg-white/10 ring-1 ring-white/10
                           hover:bg-white/15 transition"
              >
                ✕
              </button>
            </div>

            {/* form */}
            <div className="mt-5 space-y-4">
              {/* category */}
              <div>
                <div className="mb-2 text-xs font-semibold text-white/75">
                  Category
                </div>
                <select
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                  className="w-full rounded-2xl bg-white/10 text-white
                             px-4 py-3 text-sm ring-1 ring-white/15"
                >
                  <option value="" className="bg-[#061a12] text-white">
                    Select category
                  </option>
                  {options.map((c) => (
                    <option
                      key={c.id}
                      value={c.id}
                      className="bg-[#061a12] text-white"
                    >
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>

              {/* amount */}
              <div>
                <div className="mb-2 text-xs font-semibold text-white/75">
                  Amount
                </div>
                <input
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  type="number"
                  min="0"
                  step="1"
                  placeholder="e.g. 500"
                  className="w-full rounded-2xl bg-white/10 text-white
                             px-4 py-3 text-sm ring-1 ring-white/15"
                />
              </div>

              {/* note */}
              <div>
                <div className="mb-2 text-xs font-semibold text-white/75">
                  Note (optional)
                </div>
                <input
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  placeholder="Reason / note"
                  className="w-full rounded-2xl bg-white/10 text-white
                             px-4 py-3 text-sm ring-1 ring-white/15"
                />
              </div>

              {/* message */}
              {msg && (
                <div className="rounded-2xl bg-white/5 px-4 py-3 text-sm
                                ring-1 ring-white/10">
                  {msg}
                </div>
              )}

              {/* remaining */}
              <div className="text-sm text-white/70">
                Remaining to assign:{" "}
                <span className="font-semibold text-lime-200">
                  ৳{safeRemaining.toLocaleString()}
                </span>
              </div>

              {/* submit */}
              <button
                onClick={submit}
                disabled={loading}
                className="w-full rounded-2xl bg-lime-300 py-3
                           text-sm font-semibold text-[#061a12]
                           hover:bg-lime-200 transition
                           disabled:opacity-60"
              >
                {loading ? "Assigning..." : "Assign"}
              </button>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
