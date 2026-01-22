import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
  } from "recharts";
  
  const data = [
    { day: "1", spent: 200, budget: 250 },
    { day: "5", spent: 400, budget: 500 },
    { day: "10", spent: 700, budget: 750 },
    { day: "15", spent: 900, budget: 900 },
    { day: "20", spent: 1200, budget: 1100 },
  ];
  
  export default function SpendingLineChart() {
    return (
      <div className="h-[280px]">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data}>
            <XAxis dataKey="day" stroke="#ffffff70" />
            <YAxis stroke="#ffffff70" />
            <Tooltip />
            <Line
              type="monotone"
              dataKey="budget"
              stroke="#bef264"
              strokeDasharray="5 5"
            />
            <Line
              type="monotone"
              dataKey="spent"
              stroke="#22c55e"
              strokeWidth={3}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  }
  