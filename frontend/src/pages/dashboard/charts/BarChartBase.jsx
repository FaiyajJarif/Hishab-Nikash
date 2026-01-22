import {
    ResponsiveContainer,
    BarChart,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
    Bar,
  } from "recharts";
  
  export default function BarChartBase({ data }) {
    return (
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 10, right: 10, bottom: 0, left: 0 }}>
          <CartesianGrid strokeDasharray="4 4" opacity={0.15} />
          <XAxis dataKey="label" tick={{ fill: "rgba(255,255,255,0.6)", fontSize: 11 }} />
          <YAxis tick={{ fill: "rgba(255,255,255,0.6)", fontSize: 11 }} />
          <Tooltip
            contentStyle={{
              background: "rgba(10, 30, 22, 0.95)",
              border: "1px solid rgba(255,255,255,0.15)",
              borderRadius: 14,
              color: "white",
            }}
          />
          <Bar dataKey="value" radius={[10, 10, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    );
  }
  