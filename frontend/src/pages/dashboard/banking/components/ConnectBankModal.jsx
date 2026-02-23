import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { bankingApi } from "../../../../api/bankingApi";

export default function ConnectBankModal({ open, onClose, onSuccess }) {
  const [provider, setProvider] = useState("BKASH");
  const [accountNumber, setAccountNumber] = useState("");
  const [pin, setPin] = useState("");
  const [loading, setLoading] = useState(false);

  // Reset form when modal opens
  useEffect(() => {
    if (!open) return;

    setProvider("BKASH");
    setAccountNumber("");
    setPin("");
    setLoading(false);
  }, [open]);

  if (!open) return null;

  async function handleConnect() {
    if (!accountNumber.trim()) {
      return toast.error("Enter account number");
    }

    if (!pin.trim()) {
      return toast.error("Enter PIN");
    }

    try {
      setLoading(true);

      await bankingApi.connect({
        provider,
        accountNumber,
        pin
      });

      toast.success("Bank connected ✅");

      onSuccess?.(); // refresh banks list
      onClose();
    } catch (e) {
      console.error(e);
      toast.error("Connect bank failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="w-full max-w-md rounded-3xl bg-[#0b241a] p-6 ring-1 ring-white/15"
      >
        <div className="text-xl font-bold text-lime-200">
          Connect Bank
        </div>

        <div className="mt-1 text-sm text-white/60">
          Link an existing mock bank account
        </div>

        {/* Provider */}
        <select
          value={provider}
          onChange={(e) => setProvider(e.target.value)}
          className="mt-4 w-full rounded-2xl bg-white/10 px-4 py-3 text-white outline-none ring-1 ring-white/15"
        >
          <option value="BKASH">BKASH</option>
          <option value="NAGAD">NAGAD</option>
          <option value="DBBL">DBBL</option>
        </select>

        {/* Account Number */}
        <input
          value={accountNumber}
          onChange={(e) => setAccountNumber(e.target.value)}
          placeholder="Account number"
          className="mt-4 w-full rounded-2xl bg-white/10 px-4 py-3 text-white outline-none ring-1 ring-white/15"
        />

        {/* PIN */}
        <input
          type="password"
          value={pin}
          onChange={(e) => setPin(e.target.value)}
          placeholder="Enter PIN"
          className="mt-4 w-full rounded-2xl bg-white/10 px-4 py-3 text-white outline-none ring-1 ring-white/15"
        />

        {/* Buttons */}
        <div className="mt-6 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="rounded-2xl px-4 py-2 text-sm bg-white/10 ring-1 ring-white/15"
          >
            Cancel
          </button>

          <button
            onClick={handleConnect}
            disabled={loading}
            className="rounded-2xl bg-lime-300 px-5 py-2 text-sm font-semibold text-[#061a12] hover:bg-lime-200 disabled:opacity-50"
          >
            {loading ? "Connecting..." : "Connect"}
          </button>
        </div>
      </motion.div>
    </div>
  );
}
