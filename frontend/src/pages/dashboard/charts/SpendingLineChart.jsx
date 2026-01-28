import { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import { dashboardApi } from "../api/dashboardApi";
import "../api/chartSetup"; // 🔑 REQUIRED

export default function SpendingLineChart({ days = 14 }) {
  const [data, setData] = useState([]);

  useEffect(() => {
    const today = new Date().toISOString().slice(0, 10);
  
    dashboardApi
      .rollingAverage(days, today)
      .then((res) => {
        console.log("📈 rolling data", res);
        setData(res);
      })
      .catch(console.error);
  }, [days]);  

  if (!data.length) {
    return (
      <div className="h-full flex items-center justify-center text-white/60">
        No spending data
      </div>
    );
  }

  return (
    <div className="h-full px-4 pb-4">
      <Line
        data={{
          labels: data.map(d => d.date),
          datasets: [
            {
              data: data.map(d => d.average),
              borderColor: "#a3e635",
              backgroundColor: "rgba(163,230,53,0.25)",
              tension: 0.35,
              fill: true,
              pointRadius: 0,
            },
          ],
        }}
        options={{
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            x: { grid: { display: false } },
            y: {
              grid: { color: "rgba(255,255,255,0.08)" },
              ticks: { color: "rgba(255,255,255,0.6)" },
            },
          },
        }}
      />
    </div>
  );
}
