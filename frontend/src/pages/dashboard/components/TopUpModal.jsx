import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { bankingApi } from "../../../api/bankingApi";

export default function TopUpModal({ open, onClose, onSuccess }) {
  const [amount, setAmount] = useState("");
  const [banks, setBanks] = useState([]);
  const [bankId, setBankId] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!open) return;
  
    async function loadBanks() {
      try {
        const list = await bankingApi.getBanks();
        setBanks(list);
  
        if (list.length > 0) {
          setBankId(list[0].bankAccountId);
        }
        else {
          setBankId("");
        }
      } catch (err) {
        console.error(err);
        setBanks([]);
        setBankId("");
        toast.error("Failed to load banks");
      }
    }
  
    loadBanks();
  }, [open]);  

    if (!open) return null;

  async function handleSubmit() {
    if (!amount || Number(amount) <= 0) {
      toast.error("Enter valid amount");
      return;
    }

    if (!bankId) {
      toast.error("Select a bank");
      return;
    }

    try {
      setLoading(true);

      await bankingApi.topUp({
        bankAccountId: Number(bankId),
        amount: Number(amount),
      });

      toast.success("Wallet topped up from bank");

      onSuccess?.();
      onClose();
      setAmount("");
    } catch (e) {
      toast.error(
        e?.response?.data?.error?.message || "Top up failed"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="w-full max-w-md rounded-3xl bg-[#0b241a] p-6 ring-1 ring-white/15"
      >
        <div className="text-xl font-bold text-lime-200">
          Top Up Wallet
        </div>

        {/* 🔥 BANK SELECT */}
        {banks.length > 0 ? (
          <select
            value={bankId}
            onChange={(e) => setBankId(e.target.value)}
            className="mt-4 w-full rounded-2xl bg-white/10 px-4 py-3 text-white outline-none ring-1 ring-white/15"
          >
            {banks.map((b) => (
              <option
                key={b.bankAccountId || b.id}
                value={b.bankAccountId || b.id}
              >
                {b.provider} (৳{Number(b.balance).toLocaleString()})
              </option>
            ))}
          </select>
        ) : (
          <div className="mt-4 text-red-300 text-sm">
            No bank connected.
            <button
              onClick={() => {
                onClose();
                window.dispatchEvent(new Event("open-connect-bank"));
              }}
              className="ml-2 underline text-lime-200"
            >
              Connect now
            </button>
          </div>
        )}

        {/* 🔥 AMOUNT INPUT */}
        <input
          type="number"
          placeholder="Enter amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="mt-4 w-full rounded-2xl bg-white/10 px-4 py-3 text-white outline-none ring-1 ring-white/15"
        />

        {/* 🔥 BUTTONS */}
        <div className="mt-6 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="rounded-2xl px-4 py-2 text-sm bg-white/10 ring-1 ring-white/15"
          >
            Cancel
          </button>

          <button
            onClick={handleSubmit}
            disabled={loading || banks.length === 0}
            className="rounded-2xl bg-lime-300 px-5 py-2 text-sm font-semibold text-[#061a12] hover:bg-lime-200 disabled:opacity-50"
          >
            {loading ? "Processing..." : "Confirm"}
          </button>
        </div>
      </motion.div>
    </div>
  );
}
