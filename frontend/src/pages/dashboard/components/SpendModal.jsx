import { useState, useEffect } from "react";
import { dashboardApi } from "../api/dashboardApi";
import { walletApi } from "../../../api/walletApi";
import { bankingApi } from "../../../api/bankingApi";

export default function SpendModal({
  open,
  onClose,
  onSuccess,
  category,
  month,
  year,
}) {
  const [amount, setAmount] = useState("");
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("WALLET");
  const [wallet, setWallet] = useState(null);
  const [banks, setBanks] = useState([]);

  // ✅ ALL HOOKS FIRST
  useEffect(() => {
    if (!open) return;

    async function loadData() {
      try {
        const w = await walletApi.getWallet();
        setWallet(w.data);

        const b = await bankingApi.getBanks();
        setBanks(b.data);
      } catch (e) {
        console.error("Failed loading balances");
      }
    }

    loadData();
  }, [open]);

  // ✅ CONDITIONAL RENDER AFTER HOOKS
  if (!open) return null;

  async function submit() {
    if (!amount || Number(amount) <= 0) return;

    setLoading(true);

    try {
      await dashboardApi.addTransaction({
        categoryId: category.id,
        amount: Number(amount),
        month,
        year,
        note,
        paymentMethod,
      });

      window.dispatchEvent(new Event("dashboard-refresh"));
      if (onSuccess) onSuccess();
      onClose();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/60 z-50 grid place-items-center">
      <div className="w-full max-w-sm rounded-3xl bg-[#061a12] p-6 ring-1 ring-white/15">
        <h2 className="text-lg font-bold text-white">
          Add Spent — {category.name}
        </h2>

        <input
          type="number"
          className="mt-4 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <input
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
          placeholder="Note (optional)"
          value={note}
          onChange={(e) => setNote(e.target.value)}
        />

        {/* PAYMENT METHOD */}
        <div className="mt-4">
          <div className="text-sm text-white/70 mb-2">
            Payment Method
          </div>

          <div className="flex gap-2">
            {["WALLET", "BANK"].map((method) => (
              <button
                key={method}
                type="button"
                onClick={() => setPaymentMethod(method)}
                className={`flex-1 rounded-2xl px-4 py-2 text-sm transition ${
                  paymentMethod === method
                    ? "bg-lime-300 text-[#061a12] font-semibold"
                    : "bg-white/10 text-white/70 ring-1 ring-white/15"
                }`}
              >
                {method}
              </button>
            ))}
          </div>

          {/* BALANCE DISPLAY */}
          {paymentMethod === "WALLET" && wallet && (
            <div className="mt-2 text-xs text-lime-200">
              Wallet Balance: ৳{Number(wallet.balance).toLocaleString()}
            </div>
          )}

          {paymentMethod === "BANK" && banks.length > 0 && (
            <div className="mt-2 text-xs text-blue-200">
              Bank Balance: ৳
              {Number(banks[0].mockBalance).toLocaleString()}
            </div>
          )}
        </div>

        <div className="mt-5 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 rounded-xl bg-white/10 py-2"
          >
            Cancel
          </button>

          <button
            onClick={submit}
            disabled={loading}
            className="flex-1 rounded-xl bg-red-400 text-white font-semibold"
          >
            {loading ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
