import { useState } from "react";

export default function FamilyAddExpenseModal({
  category,
  month,
  year,
  onSave,
  onClose
}) {
  const [amount, setAmount] = useState("");

  async function submit() {
    const value = Number(amount);
    if (!value || value <= 0) return alert("Enter valid amount");

    await onSave(value);
    onClose();
  }

  return (
    <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center">
      <div className="w-full max-w-md rounded-3xl bg-[#0b0f14] p-6 ring-1 ring-white/15 space-y-4">
        <div className="text-lg font-semibold">
          💸 Add Expense — {category.categoryName}
        </div>

        <input
          type="number"
          min="0"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="w-full rounded-xl bg-black/30 px-4 py-3 text-sm ring-1 ring-white/15"
          placeholder="Expense amount"
        />

        <div className="flex justify-end gap-2 pt-2">
          <button
            onClick={onClose}
            className="rounded-xl bg-white/10 px-4 py-2 text-sm"
          >
            Cancel
          </button>

          <button
            onClick={submit}
            className="rounded-xl bg-lime-400 px-4 py-2 text-sm font-semibold text-black"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
}
