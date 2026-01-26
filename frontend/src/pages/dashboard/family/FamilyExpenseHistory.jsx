import { useEffect, useState } from "react";
import { familyApi } from "../../../api/familyApi";

export default function FamilyExpenseHistory({ familyId, refreshKey }) {
  const [expenses, setExpenses] = useState([]);

  async function load() {
    const res = await familyApi.getExpenses(familyId);
    console.log("EXPENSE HISTORY API:", res);
    setExpenses(res || []);
  }

  useEffect(() => {
    load();
  }, [familyId, refreshKey]);  

  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
      <div className="text-lg font-semibold mb-4">💸 Expense History</div>

      <div className="space-y-3">
        {expenses.map(e => (
          <div
            key={e.id}
            className="flex justify-between text-sm bg-white/5 p-3 rounded-xl"
          >
            <div>
              <div className="font-medium">
                ৳{e.amount.toLocaleString()}
              </div>
              <div className="text-xs text-white/60">
                {e.categoryName}
              </div>
              {e.note && (
                <div className="text-xs italic text-white/50">
                  “{e.note}”
                </div>
              )}
            </div>

            <div className="text-right text-xs text-white/50">
              {e.userName}<br />
              {new Date(e.spentAt).toLocaleString()}
            </div>
          </div>
        ))}

        {!expenses.length && (
          <div className="text-sm text-white/60">
            No expenses yet.
          </div>
        )}
      </div>
    </div>
  );
}
