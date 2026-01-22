// src/pages/dashboard/sections/ProfileDashboard.jsx
import { motion } from "framer-motion";

export default function ProfileDashboard() {
  const stats = [
    { label: "Total spent", value: "৳24,580" },
    { label: "Active budgets", value: "12" },
    { label: "Goals achieved", value: "3" },
  ];

  return (
    <div className="space-y-6">
      <Header
        title="Profile"
        subtitle="Your personal finance footprint"
      />

      <motion.div
        initial={{ opacity: 0, scale: 0.96 }}
        animate={{ opacity: 1, scale: 1 }}
        className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15"
      >
        <div className="text-xl font-extrabold">Faiyaj Jarif</div>
        <div className="mt-1 text-sm text-white/60">Owner · Family admin</div>
      </motion.div>

      <div className="grid gap-4 md:grid-cols-3">
        {stats.map((s, i) => (
          <motion.div
            key={s.label}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.05 }}
            className="rounded-2xl bg-white/10 p-5 ring-1 ring-white/15"
          >
            <div className="text-xs text-white/60">{s.label}</div>
            <div className="mt-2 text-2xl font-extrabold text-lime-200">
              {s.value}
            </div>
          </motion.div>
        ))}
      </div>

      <button className="rounded-2xl bg-white/10 px-6 py-3 ring-1 ring-white/15 hover:bg-white/15 transition">
        Account settings
      </button>
    </div>
  );
}

function Header({ title, subtitle }) {
  return (
    <div>
      <h1 className="text-2xl font-extrabold">{title}</h1>
      <p className="mt-1 text-sm text-white/60">{subtitle}</p>
    </div>
  );
}
