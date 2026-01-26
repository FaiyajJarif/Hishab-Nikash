import { Line } from "react-chartjs-2";

export default function DailySpendingTrend({
  calendar,
  rollingSeries,
}) {
  if (!calendar || calendar.length === 0) return null;

  const labels = calendar.map(d => d.date);

  return (
    <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
      <div className="mb-3 text-sm font-semibold">
        Daily Spend vs Rolling Average
      </div>

      <Line
        data={{
          labels,
          datasets: [
            {
              label: "Daily Spend",
              data: calendar.map(d => d.expense),
              borderColor: "#a3e635",
              backgroundColor: "rgba(163,230,53,0.25)",
              pointRadius: 4,
              tension: 0.35,
            },
            {
              label: "Rolling Avg",
              data: rollingSeries.map(d => d.amount),
              borderColor: "#94a3b8",
              borderDash: [6, 6],
              pointRadius: 0,
              tension: 0.35,
            },
          ],
        }}
        options={{
          plugins: { legend: { display: false } },
          scales: {
            y: {
              ticks: { callback: v => `৳${v}` },
            },
            x: { display: false },
          },
        }}
      />
    </div>
  );
}
