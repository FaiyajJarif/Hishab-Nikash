x// src/pages/dashboard/components/IncomeInputCard.jsx
import { useState } from "react";
import { apiRequest } from "../../../lib/api";

export default function IncomeInputCard({ month, year, current }) {
  const [income, setIncome] = useState(current || "");
  const [saving, setSaving] = useState(false);

  async function save() {
    setSaving(true);
    await apiRequest("/api/budget/income", {
      method: "POST",
      body: { month, year, income: Number(income) },
    });
    setSaving(false);
  }

  return (
    <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
      <div className="text-sm font-semibold text-white/85">Monthly Income</div>
      <div className="mt-3 flex gap-3">
        <input
          type="number"
          value={income}
          onChange={(e) => setIncome(e.target.value)}
          className="w-full rounded-xl bg-black/30 px-4 py-2 text-white outline-none"
          placeholder="Enter income"
        />
        <button
          onClick={save}
          disabled={saving}
          className="rounded-xl bg-lime-300 px-4 font-semibold text-[#061a12]"
        >
          {saving ? "Saving…" : "Save"}
        </button>
      </div>
    </div>
  );
}
