import { useState } from "react";
import { dashboardApi } from "../api/dashboardApi";

export default function TargetModal({ open, category, month, year, onClose }) {
  const [monthlyAmount, setMonthlyAmount] = useState("");
  const [totalAmount, setTotalAmount] = useState("");
  const [type, setType] = useState("MONTHLY");

  if (!open) return null;

  async function save() {
    await dashboardApi.setTarget({
      categoryId: category.id,
      month,
      year,
      amount: Number(monthlyAmount),
      frequency: type,
      totalTargetAmount: type === "TOTAL" ? Number(totalAmount) : null,
    });

    window.dispatchEvent(new Event("dashboard-refresh"));
    onClose();
  }

  return (
    <div className="fixed inset-0 bg-black/60 z-50 grid place-items-center">
      <div className="w-full max-w-sm rounded-3xl bg-[#061a12] p-6 ring-1 ring-white/15">
        <h2 className="text-lg font-bold text-white">Set target</h2>

        {/* TARGET TYPE */}
        <select
          className="mt-4 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
          value={type}
          onChange={(e) => setType(e.target.value)}
        >
          <option value="MONTHLY">₹ per month</option>
          <option value="TOTAL">₹ per month until total</option>
        </select>

        {/* MONTHLY AMOUNT */}
        <input
          type="number"
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
          placeholder="Monthly amount (e.g. 1000)"
          value={monthlyAmount}
          onChange={(e) => setMonthlyAmount(e.target.value)}
        />

        {/* TOTAL AMOUNT */}
        {type === "TOTAL" && (
          <input
            type="number"
            className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2 text-white"
            placeholder="Total target (e.g. 50000)"
            value={totalAmount}
            onChange={(e) => setTotalAmount(e.target.value)}
          />
        )}

        <div className="mt-5 flex gap-3">
          <button onClick={onClose} className="flex-1 rounded-xl bg-white/10 py-2">
            Cancel
          </button>
          <button
            onClick={save}
            className="flex-1 rounded-xl bg-lime-300 text-[#061a12] font-semibold"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
}
