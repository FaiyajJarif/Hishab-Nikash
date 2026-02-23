import { useState } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { walletApi } from "../../../api/walletApi";

export default function TransferModal({ open, onClose, onSuccess }) {
  const [email, setEmail] = useState("");
  const [amount, setAmount] = useState("");
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(false);

  if (!open) return null;

  async function handleTransfer() {
    try {
      setLoading(true);

      const idempotencyKey =
        "TXN-" + Date.now() + "-" + Math.random();

      await walletApi.transfer({
        toEmail: email,
        amount: Number(amount),
        note,
        idempotencyKey,
      });

      onSuccess();
      onClose();
    } catch (e) {
      toast.error(
        e?.response?.data?.error?.message ||
        "Transfer failed"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="bg-[#061a12] rounded-3xl p-6 w-[420px] ring-1 ring-white/15"
      >
        <div className="text-lg font-bold mb-4">
          Send Money
        </div>

        <input
          type="email"
          placeholder="Recipient Email"
          className="w-full mb-3 px-4 py-2 rounded-xl bg-white/10 text-white"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          placeholder="Amount"
          type="number"
          className="w-full mb-3 px-4 py-2 rounded-xl bg-white/10 text-white"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <input
          placeholder="Note (optional)"
          className="w-full mb-4 px-4 py-2 rounded-xl bg-white/10 text-white"
          value={note}
          onChange={(e) => setNote(e.target.value)}
        />

        <div className="flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-white/10"
          >
            Cancel
          </button>
          <button
            onClick={handleTransfer}
            disabled={loading}
            className="px-4 py-2 rounded-xl bg-lime-300 text-[#061a12] font-semibold"
          >
            {loading ? "Sending..." : "Send"}
          </button>
        </div>
      </motion.div>
    </div>
  );
}
