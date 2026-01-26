export default function DailyCalendarHeatmap({ days, onSelect }) {
    if (!Array.isArray(days) || days.length === 0) return null;
  
    const expenses = days.map(d => d.expense);
    const max = Math.max(...expenses);
  
    function intensity(expense) {
      if (expense === 0) return "bg-white/5";
  
      const sorted = [...expenses].sort((a, b) => a - b);
      const p25 = sorted[Math.floor(sorted.length * 0.25)];
      const p50 = sorted[Math.floor(sorted.length * 0.5)];
      const p75 = sorted[Math.floor(sorted.length * 0.75)];
  
      if (expense <= p25) return "bg-lime-400/30";
      if (expense <= p50) return "bg-lime-400/50";
      if (expense <= p75) return "bg-orange-400/50";
      return "bg-red-400/60";
    }
  
    return (
      <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
        <div className="mb-3 text-sm font-semibold">Monthly Activity</div>
  
        <div className="grid grid-cols-7 gap-2">
          {days.map(d => (
            <div
              key={d.date}
              onClick={() => onSelect(new Date(d.date))}
              className={`h-9 rounded-lg cursor-pointer transition hover:ring-2 hover:ring-lime-300/50 ${intensity(d.expense)}`}
              title={`${d.date} — ৳${d.expense}`}
            />
          ))}
        </div>
      </div>
    );
  }
  