import { motion } from "framer-motion";

export default function SummaryCards({ totals, mode }) {
  const cards = [
    // ✅ NEW: GLOBAL INCOME
    {
      label: "Income",
      value: money(totals.income),
      hint: "Total this month",
      highlight: true,
    },

    {
      label: "Assigned",
      value: money(totals.assigned),
      hint: mode === "month" ? "This month" : "Today",
    },
    {
      label: "Spent",
      value: money(totals.spent),
      hint: "Activity",
    },
    {
      label: "Available",
      value: money(totals.available),
      hint: "Remaining",
    },
    {
      label: "Overspent",
      value: money(totals.overspent),
      hint: "Needs attention",
      danger: true,
    },
  ];

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
      {cards.map((c, i) => (
        <motion.div
          key={c.label}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.05 * i, duration: 0.35 }}
          className={[
            "rounded-3xl bg-white/10 backdrop-blur-xl p-5 ring-1 ring-white/15 shadow-[0_20px_60px_rgba(0,0,0,0.25)]",
            c.danger ? "ring-red-200/20" : "",
            c.highlight ? "ring-lime-300/30" : "",
          ].join(" ")}
        >
          <div className="text-xs font-semibold uppercase tracking-wider text-white/60">
            {c.label}
          </div>

          <div
            className={[
              "mt-2 text-2xl font-extrabold",
              c.danger
                ? "text-red-200"
                : c.highlight
                ? "text-lime-300"
                : "text-lime-200",
            ].join(" ")}
          >
            {c.value}
          </div>

          <div className="mt-2 text-xs text-white/55">{c.hint}</div>
        </motion.div>
      ))}
    </div>
  );
}

function money(n) {
  const sign = n < 0 ? "-" : "";
  const v = Math.abs(n);
  return `${sign}৳${v.toLocaleString()}`;
}
