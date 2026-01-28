import { useEffect, useState } from "react";
import LineChartBase from "../charts/LineChartBase";
import { analyticsApi } from "../../../api/analyticsApi";

export default function SpendingIncomeChart({ days }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);

    const endDate = new Date().toISOString().slice(0, 10);

    analyticsApi
      .rollingAverage(days, endDate)
      .then((res) => {
        // ✅ res is already an array
        const formatted = res.map((d) => ({
          label: d.date.slice(5),   // MM-DD
          expense: d.average,       // ✅ FIXED
          income: 0,                // placeholder
        }));

        setData(formatted);
      })
      .finally(() => setLoading(false));
  }, [days]);

  if (loading) return <div className="text-white/60">Loading chart…</div>;
  if (!data.length) return <div className="text-white/60">No data</div>;

  return (
    <div className="h-[220px]"> {/* 🔑 REQUIRED */}
      <LineChartBase data={data} />
    </div>
  );
}
