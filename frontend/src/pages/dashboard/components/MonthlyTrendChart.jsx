import {
    Chart as ChartJS,
    BarElement,
    CategoryScale,
    LinearScale,
    Tooltip,
  } from "chart.js";
  import { Bar } from "react-chartjs-2";
  
  ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip);
  
  export default function MonthlyTrendChart({ trend }) {
    if (!trend) return null;
  
    return (
      <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
        <div className="mb-3 text-sm font-semibold">
          Weekly Spending Trend
        </div>
  
        <Bar
          data={{
            labels: trend.labels,
            datasets: [
              {
                data: trend.values,
                backgroundColor: "#a3e635",
                borderRadius: 8,
              },
            ],
          }}
          options={{
            plugins: { legend: { display: false } },
            scales: {
              y: {
                ticks: {
                  callback: (v) => `৳${v}`,
                },
              },
            },
          }}
        />
      </div>
    );
  }
  