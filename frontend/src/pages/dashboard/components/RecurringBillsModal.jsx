import { AnimatePresence, motion } from "framer-motion";
import RecurringBillsCard from "./RecurringBillsCard";
import AddRecurringBillModal from "./AddRecurringBillModal";
import { useState } from "react";

export default function RecurringBillsModal({
  open,
  onClose,
  categories,
}) {
  const [addOpen, setAddOpen] = useState(false);

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            className="fixed inset-0 bg-black/60 z-50"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />

          <motion.div
            className="fixed inset-0 z-50 flex items-center justify-center"
            initial={{ scale: 0.95, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.95, opacity: 0 }}
          >
            <div className="w-full max-w-2xl rounded-3xl bg-[#061a12] p-6 ring-1 ring-white/10 shadow-2xl">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg font-bold text-white">
                  🔁 Recurring Bills
                </h2>

                <div className="flex gap-2">
                  <button
                    onClick={() => setAddOpen(true)}
                    className="rounded-xl bg-lime-300 px-3 py-1 text-sm font-semibold text-[#061a12]"
                  >
                    + Add
                  </button>

                  <button
                    onClick={onClose}
                    className="h-8 w-8 rounded-xl bg-white/10"
                  >
                    ✕
                  </button>
                </div>
              </div>

              <RecurringBillsCard categories={categories} />
            </div>
          </motion.div>

          <AddRecurringBillModal
            open={addOpen}
            onClose={() => setAddOpen(false)}
            categories={categories}
          />
        </>
      )}
    </AnimatePresence>
  );
}
