export default function SnapshotHistory({ snapshots, onSelect }) {
    return (
      <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
        <div className="mb-3 text-sm font-semibold">
          Closed Months
        </div>
  
        <div className="space-y-2">
          {snapshots.map(s => (
            <div
              key={s.id}
              onClick={() => onSelect(s)}
              className="flex justify-between rounded-xl bg-white/5 px-4 py-3 hover:bg-white/10 cursor-pointer transition"
            >
              <span>
                {new Date(s.year, s.month - 1).toLocaleString("default", {
                  month: "long",
                  year: "numeric",
                })}
              </span>
              <span className="text-lime-300 font-bold">
                ৳{s.expense.toLocaleString()}
              </span>
            </div>
          ))}
        </div>
      </div>
    );
  }
  