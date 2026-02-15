import { useState } from "react";
import { dashboardApi } from "../api/dashboardApi";

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
