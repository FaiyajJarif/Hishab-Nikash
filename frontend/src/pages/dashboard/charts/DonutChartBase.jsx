import { ResponsiveContainer, PieChart, Pie, Tooltip, Cell } from "recharts";

export default function DonutChartBase({ data }) {
  const colors = [
    "#bef264",
    "#6ee7b7",
    "#a7f3d0",
    "#99f6e4",
    "#86efac",
    "#bbf7d0",
  ];

  return (
    <ResponsiveContainer width="100%" height="100%">
      <PieChart>
        <Tooltip
          contentStyle={{
            background: "rgba(10, 30, 22, 0.95)",
            border: "1px solid rgba(255,255,255,0.15)",
            borderRadius: 14,
            color: "white",
          }}
        />
        <Pie data={data} dataKey="value" nameKey="name" innerRadius={60} outerRadius={90}>
          {data.map((entry, index) => (
            <Cell
              key={`cell-${entry.name}-${index}`}
              fill={colors[index % colors.length]}
            />
          ))}
        </Pie>

      </PieChart>
    </ResponsiveContainer>
  );
}
