// src/pages/dashboard/sections/BankingDashboard.jsx
import { motion } from "framer-motion";

export default function BankingDashboard() {
  const banks = [
    { name: "Dutch Bangla Bank", balance: 24500 },
    { name: "bKash Wallet", balance: 6200 },
    { name: "Cash", balance: 1800 },
  ];

  return (
    <div className="space-y-6">
      <Header
        title="Banking"
        subtitle="Connected accounts & balances"
      />

      <div className="grid gap-6 md:grid-cols-2">
        {banks.map((b, i) => (
          <motion.div
            key={b.name}
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.05 }}
            className="rounded-3xl bg-white/10 backdrop-blur-xl p-6 ring-1 ring-white/15"
          >
            <div className="text-sm text-white/60">{b.name}</div>
            <div className="mt-2 text-2xl font-extrabold text-lime-200">
              ৳{b.balance.toLocaleString()}
            </div>

            <button className="mt-4 rounded-2xl bg-white/10 px-4 py-2 text-xs ring-1 ring-white/15 hover:bg-white/15 transition">
              View transactions
            </button>
          </motion.div>
        ))}
      </div>

      <button className="rounded-2xl bg-lime-300 px-6 py-3 font-semibold text-[#061a12] hover:bg-lime-200 transition">
        + Connect new bank
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
