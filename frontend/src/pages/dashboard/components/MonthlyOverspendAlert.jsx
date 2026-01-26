export default function MonthlyOverspendAlert({ alerts }) {
    if (!alerts || alerts.length === 0) return null;
  
    return (
      <div className="rounded-3xl bg-red-500/10 p-5 ring-1 ring-red-400/30">
        <div className="mb-3 text-sm font-semibold text-red-300">
          ⚠ Overspent Categories
        </div>
  
        <div className="space-y-2">
          {alerts.map((a) => (
            <div
              key={a.categoryId}
              className="flex justify-between text-sm"
            >
              <span>{a.categoryName}</span>
              <span className="text-red-300 font-semibold">
                ৳{a.overspentAmount}
              </span>
            </div>
          ))}
        </div>
      </div>
    );
  }
  