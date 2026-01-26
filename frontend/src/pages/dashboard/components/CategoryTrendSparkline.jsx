import { useEffect, useState } from "react";
import {
  Chart as ChartJS,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
} from "chart.js";
import { Line } from "react-chartjs-2";
import { apiRequest } from "../../../lib/api";

ChartJS.register(LineElement, CategoryScale, LinearScale, PointElement, Tooltip);

export default function CategoryTrendSparkline({ categoryId, days = 14 }) {
  const [series, setSeries] = useState([]);
  const [status, setStatus] = useState("loading"); // loading | ok | empty | error

  useEffect(() => {
    if (!categoryId) return;

    let alive = true;

    async function load() {
      try {
        setStatus("loading");
        const res = await apiRequest(
          `/api/analytics/category/trend?categoryId=${categoryId}&days=${days}`
        );

        const data = res?.data?.data ?? res?.data ?? [];
        if (!alive) return;

        if (!Array.isArray(data) || data.length === 0) {
          setSeries([]);
          setStatus("empty");
          return;
        }

        setSeries(data);
        setStatus("ok");
      } catch (e) {
        if (!alive) return;
        setSeries([]);
        setStatus("error");
      }
    }

    load();
    return () => {
      alive = false;
    };
  }, [categoryId, days]);

  return (
    <div className="rounded-3xl bg-white/5 p-4 ring-1 ring-white/10">
      <div className="flex items-center justify-between">
        <div className="text-xs font-semibold uppercase tracking-wider text-white/60">
          14-day Trend
        </div>
        {status === "error" && (
          <div className="text-xs text-white/45">No trend endpoint yet</div>
        )}
      </div>

      {status === "loading" && (
        <div className="mt-3 text-sm text-white/55">Loading trend…</div>
      )}

      {(status === "empty") && (
        <div className="mt-3 text-sm text-white/55">No trend data</div>
      )}

      {status === "ok" && (
        <div className="mt-3">
          <Line
            data={{
              labels: series.map((d) => d.date),
              datasets: [
                {
                  data: series.map((d) => Number(d.amount ?? d.expense ?? 0)),
                  borderColor: "#a3e635",
                  pointRadius: 0,
                  tension: 0.35,
                },
              ],
            }}
            options={{
              plugins: {
                legend: { display: false },
                tooltip: {
                  callbacks: {
                    label: (ctx) => `৳${ctx.parsed.y}`,
                  },
                },
              },
              scales: {
                x: { display: false },
                y: { display: false },
              },
            }}
          />
        </div>
      )}
    </div>
  );
}
