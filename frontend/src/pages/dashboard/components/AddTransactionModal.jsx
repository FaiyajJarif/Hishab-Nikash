import { useState } from "react";
import { dashboardApi } from "../api/dashboardApi";

export default function AddTransactionModal({ open, categories, onClose }) {
  const [categoryId, setCategoryId] = useState("");
  const [amount, setAmount] = useState("");

  if (!open) return null;

  async function save() {
    await dashboardApi.addTransaction({
      categoryId,
      amount: Number(amount),
      date: new Date().toISOString().slice(0, 10),
      note: "",
    });
    onClose();
  }

  return (
    <div className="fixed inset-0 bg-black/60 z-50 grid place-items-center">
      <div className="w-full max-w-sm rounded-3xl bg-[#061a12] p-6 ring-1 ring-white/15">
        <h2 className="text-lg font-bold text-white">Add expense</h2>

        <select
          className="mt-4 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
          onChange={(e) => setCategoryId(e.target.value)}
        >
          <option>Select category</option>
          {categories.map(c => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>

        <input
          type="number"
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <div className="mt-5 flex gap-3">
          <button onClick={onClose} className="flex-1 rounded-xl bg-white/10 py-2">
            Cancel
          </button>
          <button onClick={save} className="flex-1 rounded-xl bg-lime-300 text-[#061a12] font-semibold">
            Save
          </button>
        </div>
      </div>
    </div>
  );
}
