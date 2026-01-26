import { Line } from "react-chartjs-2";

export default function MonthComparisonChart({ snapshots }) {
  if (!snapshots || snapshots.length < 2) return null;

  const labels = snapshots.map(s => `${s.month}/${s.year}`).reverse();
  const values = snapshots.map(s => s.expense).reverse();

  return (
    <div className="rounded-3xl bg-white/10 p-5 ring-1 ring-white/15">
      <div className="mb-3 text-sm font-semibold">
        Month-over-Month Spending
      </div>

      <Line
        data={{
          labels,
          datasets: [{
            data: values,
            borderColor: "#a3e635",
            backgroundColor: "rgba(163,230,53,0.25)",
            tension: 0.4,
            pointRadius: 4
          }]
        }}
        options={{
          plugins: { legend: { display: false } },
        }}
      />
    </div>
  );
}
