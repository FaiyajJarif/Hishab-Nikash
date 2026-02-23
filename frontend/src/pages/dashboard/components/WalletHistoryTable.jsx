import { motion } from "framer-motion";
import { useState } from "react";

export default function WalletHistoryTable({ history }) {
  const [page, setPage] = useState(0);
  const pageSize = 10;

  if (!history || history.length === 0) {
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 text-white/60">
        No wallet activity yet.
      </div>
    );
  }

  const paginated = history.slice(
    page * pageSize,
    (page + 1) * pageSize
  );

  const totalPages = Math.ceil(history.length / pageSize);

  return (
    <div className="rounded-3xl bg-white/10 ring-1 ring-white/15 overflow-hidden">

      {/* HEADER */}
      <div className="px-6 py-4 border-b border-white/10 text-sm text-white/60">
        Recent Transactions
      </div>

      {/* TRANSACTION LIST */}
      <div className="divide-y divide-white/10">
        {paginated.map((tx, i) => (
          <motion.div
            key={tx.entryId}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.03 }}
            className="px-6 py-4 flex justify-between items-center"
          >
            <div>
              <div className="text-sm font-medium">
                {formatType(tx.type)}
              </div>
              <div className="text-xs text-white/50">
                {new Date(tx.createdAt).toLocaleString()}
              </div>
            </div>

            <div className="text-right">
              <div
                className={`text-sm font-semibold ${
                  Number(tx.amount) > 0
                    ? "text-lime-300"
                    : "text-red-300"
                }`}
              >
                {Number(tx.amount) > 0 ? "+" : ""}
                ৳{Math.abs(Number(tx.amount)).toLocaleString()}
              </div>

              <StatusBadge status={tx.status} />
            </div>
          </motion.div>
        ))}
      </div>

      {/* PAGINATION FOOTER */}
      <div className="flex justify-between items-center px-6 py-4 border-t border-white/10 text-sm">

        <button
          disabled={page === 0}
          onClick={() => setPage((p) => p - 1)}
          className={`px-4 py-2 rounded-xl transition ${
            page === 0
              ? "bg-white/5 text-white/30 cursor-not-allowed"
              : "bg-white/10 hover:bg-white/15"
          }`}
        >
          Prev
        </button>

        <div className="text-white/60 text-xs">
          Page {page + 1} of {totalPages}
        </div>

        <button
          disabled={(page + 1) * pageSize >= history.length}
          onClick={() => setPage((p) => p + 1)}
          className={`px-4 py-2 rounded-xl transition ${
            (page + 1) * pageSize >= history.length
              ? "bg-white/5 text-white/30 cursor-not-allowed"
              : "bg-white/10 hover:bg-white/15"
          }`}
        >
          Next
        </button>
      </div>
    </div>
  );
}

/* ---------- helpers ---------- */

function formatType(type) {
  if (type === "TRANSFER_OUT") return "Sent Money";
  if (type === "TRANSFER_IN") return "Received Money";
  if (type === "TOPUP") return "Top Up";
  if (type === "WITHDRAW") return "Withdrawal";
  return type;
}

function StatusBadge({ status }) {
  const styles = {
    SUCCESS: "bg-lime-300/20 text-lime-300",
    PENDING: "bg-yellow-300/20 text-yellow-300",
    FAILED: "bg-red-300/20 text-red-300",
  };

  return (
    <span
      className={`mt-1 inline-block text-[10px] px-2 py-1 rounded-xl ${
        styles[status] || "bg-white/10 text-white/50"
      }`}
    >
      {status}
    </span>
  );
}
