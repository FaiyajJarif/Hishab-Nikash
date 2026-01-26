export default function MonthSwitcher({ month, year, onChange }) {
    function shift(delta) {
      const d = new Date(year, month - 1 + delta);
      onChange(d.getMonth() + 1, d.getFullYear());
    }
  
    return (
      <div className="flex items-center gap-3">
        <button
          onClick={() => shift(-1)}
          className="px-2 py-1 rounded bg-white/10 hover:bg-white/20"
        >
          ◀
        </button>
  
        <span className="text-sm font-semibold">
          {monthName(month)} {year}
        </span>
  
        <button
          onClick={() => shift(1)}
          className="px-2 py-1 rounded bg-white/10 hover:bg-white/20"
        >
          ▶
        </button>
      </div>
    );
  }
  
  function monthName(m) {
    return new Date(2024, m - 1).toLocaleString("default", { month: "long" });
  }
  