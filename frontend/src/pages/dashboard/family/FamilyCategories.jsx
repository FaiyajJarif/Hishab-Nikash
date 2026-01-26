import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { familyApi } from "../../../api/familyApi";
import CreateCategoryModal from "./CreateCategoryModal";
import FamilyAddExpenseModal from "./FamilyAddExpenseModal";

export default function FamilyCategories({ familyId, month, year, summary, canEdit }) {

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savingId, setSavingId] = useState(null);

  const [showCreate, setShowCreate] = useState(false);
  const [drafts, setDrafts] = useState({});
  const [renaming, setRenaming] = useState(null);

  const [addingExpense, setAddingExpense] = useState(null);

  const isFutureMonth = () => {
    const now = new Date();
    const currentYM = now.getFullYear() * 12 + now.getMonth();
    const selectedYM = year * 12 + (month - 1);
    return selectedYM > currentYM;
  };
  
  const disabled = isFutureMonth();

  async function load() {
    setLoading(true);
  
    const res = await familyApi.getBudgetItems(familyId, month, year);
  
    setCategories(res || []);
    setLoading(false);
  }  

  useEffect(() => {
    load();
  }, [familyId, month, year]);

  async function updatePlan(categoryId, amount) {
    setSavingId(categoryId);

    await familyApi.planCategory(familyId, {
      month,
      year,
      categoryId,
      amount: Number(amount),
    });

    await load();
    setSavingId(null);
  }

  async function deleteCategory(categoryId) {
    if (!confirm("Delete this category?")) return;
  
    await familyApi.deleteCategory(familyId, categoryId);
  
    await load(); // refresh list
  }  
  async function doRename() {
    if (!renaming) return;
  
    const name = (renaming.categoryName || "").trim();
    if (!name) return alert("Name required");
  
    await familyApi.renameCategory(familyId, renaming.categoryId, name);
  
    setRenaming(null);
    await load();
  }  

  if (loading) {
    return <div className="text-white/60">Loading categories…</div>;
  }

  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="text-lg font-semibold">📂 Categories</div>
        <div className="flex items-center gap-2">
            {canEdit && (
                <button
                onClick={() => {
                  console.log("Add Category clicked");
                  setShowCreate(true);
                }}
                className="rounded-lg bg-lime-400/20 px-3 py-1 text-sm text-lime-300 hover:bg-lime-400/30"
              >
                ➕ Add Category
                </button>
            )}
        </div>
      </div>
      
      {disabled && (
        <div className="rounded-xl bg-yellow-500/10 px-4 py-2 text-sm text-yellow-300">
          🔒 Future month — planning is locked
        </div>
      )}

      {/* List */}
      <div className="space-y-2">
      {categories.map((c, i) => {
  const planned = c.plannedAmount ?? 0;
  const spent = c.actualAmount ?? 0;
  const available = planned - spent;
  const overspent = available < 0;
  console.log({
    name: c.categoryName,
    planned: c.plannedAmount,
    spent: c.actualAmount,
  });
  

  return (
    <motion.div
      key={c.categoryId}
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: i * 0.03 }}
      className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
    >
      {/* Left */}
      {/* Left */}
<div>
  <div className="text-sm font-medium">{c.categoryName}</div>

  <div className="text-xs text-white/60">
    Spent ৳{spent.toLocaleString()}
  </div>
  <div className="mt-2 h-2 w-full rounded-full bg-white/10 overflow-hidden">
  <div
    className={`h-full transition-all ${progressColor(
      progressPct(spent, planned)
    )}`}
    style={{ width: `${Math.min(progressPct(spent, planned), 100)}%` }}
    />
</div>

<div className="mt-1 text-xs text-white/50">
  {planned > 0
    ? `${progressPct(spent, planned)}% of planned`
    : "No budget assigned"}
</div>

  {canEdit && (
    <div className="flex gap-2 mt-1">
      <button
        onClick={() => setRenaming(c)}
        className="text-xs text-blue-300 hover:underline"
      >
        Rename
      </button>

      <button
        onClick={() => deleteCategory(c.categoryId)}
        className="text-xs text-red-300 hover:underline"
      >
        Delete
      </button>
      <button
        onClick={() => setAddingExpense(c)}
        className="text-xs text-lime-300 hover:underline"
      >
        + Add Expense
      </button>
    </div>
  )}
</div>

      {/* Right */}
      <div className="flex items-center gap-3">
      <input
        disabled={!canEdit || disabled}
          type="number"
          min="0"
          step="1"
          value={drafts[c.categoryId] ?? planned}
          onChange={(e) => {
            setDrafts(prev => ({
              ...prev,
              [c.categoryId]: e.target.value,
            }));
          }}
          onBlur={() => {
            const raw = drafts[c.categoryId] ?? planned;
            const value = Number(raw);

            // guard against negatives / NaN
            if (Number.isNaN(value) || value < 0) {
              setDrafts(prev => {
                const copy = { ...prev };
                delete copy[c.categoryId];
                return copy;
              });
              return;
            }

            updatePlan(c.categoryId, value);

            // clear draft after save
            setDrafts(prev => {
              const copy = { ...prev };
              delete copy[c.categoryId];
              return copy;
            });
          }}
          className="w-24 rounded-xl bg-black/30 px-3 py-2 text-sm ring-1 ring-white/10"
        />

        <div
          className={`w-24 text-right text-sm font-semibold ${
            overspent ? "text-red-300" : "text-lime-300"
          }`}
        >
          ৳{available.toLocaleString()}
        </div>

        {savingId === c.categoryId && (
          <div className="text-xs text-white/50">Saving…</div>
        )}
      </div>
    </motion.div>
  );
})}

{!categories.length && (
  <div className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10 space-y-3">
    <div className="text-sm font-semibold text-white/85">
      👋 Let’s get started
    </div>

    <div className="text-sm text-white/60">
      Start by setting your monthly income and adding budget categories.
    </div>

    <div className="flex gap-2">
      <button
        onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
        className="rounded-xl bg-lime-400 px-3 py-2 text-sm font-semibold text-black"
      >
        ➕ Set Income
      </button>

      <button
        onClick={() => setShowCreateModal(true)}
        className="rounded-xl bg-white/10 px-3 py-2 text-sm hover:bg-white/20"
      >
        ➕ Add Category
      </button>
    </div>
  </div>
)}
      </div>

      {/* Footer hint */}
      <div className="text-xs text-white/60 pt-2">
        💡 Assign money until Remaining reaches zero.
      </div>
      {showCreate && (
        <CreateCategoryModal
            familyId={familyId}
            month={month}
            year={year}
            onClose={() => setShowCreate(false)}
            onCreated={load}
        />
        )}
        {renaming && (
  <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center">
    <div className="w-full max-w-md rounded-3xl bg-[#0b0f14] p-6 ring-1 ring-white/15 space-y-4">
      <div className="text-lg font-semibold">✏️ Rename Category</div>

      <input
        value={renaming.categoryName}
        onChange={(e) =>
          setRenaming(prev => ({ ...prev, categoryName: e.target.value }))
        }
        className="w-full rounded-xl bg-black/30 px-4 py-3 text-sm ring-1 ring-white/15"
        placeholder="New name"
      />

      <div className="flex justify-end gap-2 pt-2">
        <button
          onClick={() => setRenaming(null)}
          className="rounded-xl bg-white/10 px-4 py-2 text-sm"
        >
          Cancel
        </button>

        <button
          onClick={doRename}
          className="rounded-xl bg-lime-400 px-4 py-2 text-sm font-semibold text-black"
        >
          Save
        </button>
      </div>
    </div>
  </div>
)}
{addingExpense && (
  <FamilyAddExpenseModal
    category={addingExpense}
    month={month}
    year={year}
    onClose={() => setAddingExpense(null)}
    onSave={async (amount) => {
      await familyApi.spend(familyId, {
        month,
        year,
        categoryId: addingExpense.categoryId,
        amount
      });
      await load();
    }}
  />
)}
    </div>
  );
}
function progressPct(spent, planned) {
  if (!planned || planned <= 0) return 0;
  const pct = Math.round((spent / planned) * 100);
  return Math.min(150, pct); // allow overflow visually
}

function progressColor(pct) {
  if (pct < 70) return "bg-lime-400";
  if (pct < 100) return "bg-yellow-400";
  return "bg-red-400";
}