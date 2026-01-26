import {
    Chart as ChartJS,
    LineElement,
    CategoryScale,
    LinearScale,
    PointElement,
    Tooltip,
  } from "chart.js";
  import { Line } from "react-chartjs-2";
  import { motion } from "framer-motion";
  
  ChartJS.register(
    LineElement,
    CategoryScale,
    LinearScale,
    PointElement,
    Tooltip
  );
  
  export default function DailySpendingChart({ summary, insight }) {
    if (!summary || !insight) return null;
  
    const today = Number(summary.expense ?? 0);
    const avg = Number(insight.avg ?? 0);
  
    return (
      <motion.div
        layout
        whileHover={{ y: -2 }}
        className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15 hover:ring-lime-300/40 transition shadow-[0_20px_60px_rgba(0,0,0,0.25)]"
      >
        <div className="mb-3 text-sm font-semibold">Spending vs 14-day Average</div>
  
        <Line
          data={{
            labels: ["Today", "14-day Avg"],
            datasets: [
              {
                data: [today, avg],
                borderColor: "#a3e635",
                backgroundColor: "rgba(163,230,53,0.25)",
                pointRadius: 6,
                tension: 0.4,
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
              y: {
                ticks: { callback: (v) => `৳${v}` },
              },
            },
          }}
        />
      </motion.div>
    );
  }
  