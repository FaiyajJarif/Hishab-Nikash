import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
    CartesianGrid
  } from "recharts";
  
  export default function CashflowChart({ data = [] }) {
    if (!data.length) {
      return (
        <div className="h-full rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 text-sm text-white/60">
          No cashflow data yet
        </div>
      );
    }
  
    return (
      <div className="h-full rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 flex flex-col">
        <div className="text-sm font-semibold mb-4">
          💸 Cashflow (Last 6 Months)
        </div>
  
        {/* take remaining height */}
        <div className="flex-1 min-h-[280px]">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={data}
              margin={{ top: 10, right: 20, left: 0, bottom: 0 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
  
              <XAxis
                dataKey="month"
                stroke="rgba(255,255,255,0.5)"
                tickLine={false}
                axisLine={false}
                height={24}        // 👈 reduces extra reserved space
                tickMargin={8}
              />
  
              <YAxis
                domain={[
                  (min) => Math.max(0, min * 0.9),
                  (max) => max * 1.1
                ]}
                tickFormatter={(v) => `৳${(v / 1000).toFixed(0)}k`}
                stroke="rgba(255,255,255,0.4)"
                tickLine={false}
                axisLine={false}
                width={48}
              />
  
              <Tooltip
                formatter={(v, name) => [
                  `৳${Number(v).toLocaleString()}`,
                  name === "income" ? "Income" : "Expense"
                ]}
                contentStyle={{
                  background: "#04110c",
                  borderRadius: 14,
                  border: "1px solid rgba(255,255,255,0.12)"
                }}
                labelStyle={{ color: "#a7f3d0" }}
              />
  
              <Line type="monotone" dataKey="expense" stroke="#ef4444" strokeWidth={2.5} dot={{ r: 4 }} activeDot={{ r: 6 }} />
              <Line type="monotone" dataKey="income" stroke="#22c55e" strokeWidth={2.5} dot={{ r: 4 }} activeDot={{ r: 6 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    );
  }
  