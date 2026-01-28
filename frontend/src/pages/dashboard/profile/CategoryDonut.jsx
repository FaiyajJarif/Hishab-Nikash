import {
    PieChart,
    Pie,
    Cell,
    Tooltip,
    ResponsiveContainer
  } from "recharts";
  
  const COLORS = [
    "#22c55e", // green
    "#38bdf8", // blue
    "#facc15", // yellow
    "#fb7185", // pink
    "#a78bfa", // purple
    "#f97316", // orange
  ];
  
  export default function CategoryDonut({ data = [] }) {
    
    if (!data.length) {
      return (
        <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 text-sm text-white/60">
          No category spending yet.
        </div>
      );
    }
    const chartData = data.map(c => ({
        name: c.categoryName,
        value: c.amount
      }));      
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
        <div className="text-sm font-semibold text-white/85 mb-4">
          🍩 Spending by Category
        </div>
  
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={data}
                dataKey="amount"
                nameKey="name"
                cx="50%"
                cy="50%"
                innerRadius={55}
                outerRadius={85}
                paddingAngle={3}
              >
                {data.map((_, i) => (
                  <Cell
                    key={i}
                    fill={COLORS[i % COLORS.length]}
                  />
                ))}
              </Pie>
  
              <Tooltip
                formatter={(value) => [`৳${value.toLocaleString()}`, "Spent"]}
                contentStyle={{
                  backgroundColor: "#061a12",
                  border: "1px solid rgba(255,255,255,0.15)",
                  borderRadius: "8px",
                  color: "#fff",
                }}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
  
        {/* Legend */}
        <div className="mt-4 space-y-2">
        {chartData.map((c, i) => (
          <div key={c.name} className="flex items-center justify-between text-sm">
            <div className="flex items-center gap-2">
              <span
                className="h-2 w-2 rounded-full"
                style={{ background: COLORS[i % COLORS.length] }}
              />
              <span>{c.name}</span>
            </div>
            <div className="text-white/70">৳{c.value}</div>
          </div>
        ))}
      </div>
      </div>
    );
  }
  