import { useState } from "react";
import { familyApi } from "../../../api/familyApi";

export default function CreateCategoryModal({
  familyId,
  onClose,
  onCreated,
}) {
  const [name, setName] = useState("");
  const [type, setType] = useState("Expense");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  async function create() {
    if (!name.trim()) {
      setError("Category name required");
      return;
    }

    try {
      setSaving(true);
      await familyApi.createCategory(familyId, { name, type });
      setName("");
      setType("Expense");
      onCreated?.();
      onClose();
    } catch (e) {
      setError("Failed to create category");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center">
      <div className="w-full max-w-md rounded-3xl bg-[#0b0f14] p-6 ring-1 ring-white/15 space-y-4">
        <div className="text-lg font-semibold">➕ Create Category</div>

        <input
          value={name}
          onChange={e => setName(e.target.value)}
          placeholder="Category name"
          className="w-full rounded-xl bg-black/30 px-4 py-3 text-sm ring-1 ring-white/15"
        />

        <select
          value={type}
          onChange={e => setType(e.target.value)}
          className="w-full rounded-xl bg-black/30 px-4 py-3 text-sm ring-1 ring-white/15"
        >
          <option value="Expense">Expense</option>
          <option value="Savings">Savings</option>
          <option value="Debt">Debt</option>
          <option value="Other">Other</option>
        </select>

        {error && <div className="text-sm text-red-300">{error}</div>}

        <div className="flex justify-end gap-2 pt-2">
          <button
            onClick={onClose}
            className="rounded-xl bg-white/10 px-4 py-2 text-sm"
          >
            Cancel
          </button>

          <button
            onClick={create}
            disabled={saving}
            className="rounded-xl bg-lime-400 px-4 py-2 text-sm font-semibold text-black disabled:opacity-60"
          >
            {saving ? "Creating…" : "Create"}
          </button>
        </div>
      </div>
    </div>
  );
}
