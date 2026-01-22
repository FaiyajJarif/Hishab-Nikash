import { useEffect, useState } from "react";
import { dashboardApi } from "../api/dashboardApi";
import BarChartBase from "../charts/BarChartBase";

export default function MonthlyAnalytics() {
  const [month, setMonth] = useState(() => new Date().toISOString().slice(0, 7)); // YYYY-MM
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    setError("");
    dashboardApi
      .getMonthlyAnalytics({ monthISO: month })
      .then(setData)
      .catch((e) => setError(e?.message || "Failed to load monthly analytics"));
  }, [month]);

  return (
    <div className="space-y-6">
      <Header title="Monthly analytics" subtitle="Trends, highlights & month-close insights." />

      <div className="flex flex-wrap items-center gap-3">
        <input
          type="month"
          value={month}
          onChange={(e) => setMonth(e.target.value)}
          className="rounded-2xl bg-white/10 px-4 py-3 text-sm ring-1 ring-white/15 outline-none"
        />
        <button className="rounded-2xl bg-lime-300 px-4 py-3 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition">
          Manual month close
        </button>
      </div>

      {error ? <div className="text-red-200">{error}</div> : null}

      <Card>
        <div className="text-sm font-semibold text-white/85">Category ranking</div>
        <div className="mt-4 h-[280px]">
          <BarChartBase data={data?.bars || []} />
        </div>
      </Card>
    </div>
  );
}

function Header({ title, subtitle }) {
  return (
    <div>
      <div className="text-2xl font-extrabold">{title}</div>
      <div className="mt-1 text-sm text-white/65">{subtitle}</div>
    </div>
  );
}
function Card({ children }) {
  return <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">{children}</div>;
}
