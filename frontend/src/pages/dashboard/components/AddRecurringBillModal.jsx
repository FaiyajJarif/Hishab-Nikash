import { useState } from "react";
import { recurringBillApi } from "../api/recurringBillApi";

export default function AddRecurringBillModal({ open, onClose, categories }) {
  const [form, setForm] = useState({
    name: "",
    amount: "",
    frequency: "MONTHLY",
    nextDueDate: "",
    categoryId: "",
  });

  if (!open) return null;

  async function submit() {
    await recurringBillApi.create({
      ...form,
      amount: Number(form.amount),
      categoryId: Number(form.categoryId),
    });

    window.dispatchEvent(new Event("dashboard-refresh"));
    onClose();
  }

  return (
    <div className="fixed inset-0 bg-black/60 z-50 grid place-items-center">
      <div className="w-full max-w-md rounded-3xl bg-[#061a12] p-6 ring-1 ring-white/15">
        <h2 className="text-lg font-extrabold">Add Recurring Bill</h2>

        <input
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2"
          placeholder="Bill name"
          onChange={e => setForm({ ...form, name: e.target.value })}
        />

        <input
          type="number"
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2"
          placeholder="Amount"
          onChange={e => setForm({ ...form, amount: e.target.value })}
        />

        <select
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2"
          onChange={e => setForm({ ...form, frequency: e.target.value })}
        >
          <option value="MONTHLY">Monthly</option>
          <option value="WEEKLY">Weekly</option>
          <option value="DAILY">Daily</option>
        </select>

        <input
          type="date"
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2"
          onChange={e => setForm({ ...form, nextDueDate: e.target.value })}
        />

        <select
          className="mt-3 w-full rounded-xl bg-black/30 px-4 py-2"
          onChange={e => setForm({ ...form, categoryId: e.target.value })}
        >
          <option value="">Select category</option>
          {categories.map(c => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>

        <div className="mt-5 flex gap-3">
          <button onClick={onClose} className="flex-1 rounded-xl bg-white/10 py-2">
            Cancel
          </button>
          <button
            onClick={submit}
            className="flex-1 rounded-xl bg-lime-300 text-[#061a12] font-semibold"
          >
            Add
          </button>
        </div>
      </div>
    </div>
  );
}
