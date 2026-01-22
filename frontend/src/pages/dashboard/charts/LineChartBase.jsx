import {
    ResponsiveContainer,
    LineChart,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
    Line,
  } from "recharts";
  
  export default function LineChartBase({ data }) {
    return (
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data} margin={{ top: 10, right: 10, bottom: 0, left: 0 }}>
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
          <Line type="monotone" dataKey="expense" strokeWidth={2} dot={false} />
          <Line type="monotone" dataKey="income" strokeWidth={2} dot={false} />
        </LineChart>
      </ResponsiveContainer>
    );
  }
  