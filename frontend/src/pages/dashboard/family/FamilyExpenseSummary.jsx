import { useEffect, useMemo, useState } from "react";
import { familyApi } from "../../../api/familyApi";

export default function FamilyExpenseSummary({ familyId, month, year }) {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    if (!familyId) return;
    familyApi
      .getExpenseSummary(familyId, month, year)
      .then(res => setRows(res || []));
  }, [familyId, month, year]);

  const maxTotal = useMemo(() => {
    return Math.max(0, ...rows.map(r => Number(r.total)));
  }, [rows]);

  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
      <div className="text-lg font-semibold mb-4 flex items-center gap-2">
        📊 Monthly Spending
      </div>

      {!rows.length && (
        <div className="text-sm text-white/60">
          No expenses this month.
        </div>
      )}

      <div className="space-y-4">
        {rows.map((r, i) => {
          const percent =
            maxTotal === 0 ? 0 : (Number(r.total) / maxTotal) * 100;

          return (
            <div key={i} className="space-y-1">
              <div className="flex justify-between text-sm">
                <span className="text-white/80">
                  {r.categoryName}
                </span>
                <span className="font-semibold">
                  ৳{Number(r.total).toLocaleString()}
                </span>
              </div>

              {/* progress bar */}
              <div className="h-2 rounded-full bg-white/10 overflow-hidden">
                <div
                  className="h-full rounded-full bg-lime-400/80 transition-all"
                  style={{ width: `${percent}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
