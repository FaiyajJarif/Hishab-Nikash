import { useMemo, useState } from "react";
import { motion } from "framer-motion";
import { useDashboardData } from "../hooks/useDashboardData";

import CategoryDrawer from "../components/CategoryDrawer";
import AssignMoneyModal from "../components/AssignMoneyModal";
import SetIncomeModal from "../components/SetIncomeModal";

import SummaryCards from "../components/SummaryCards";
import SpendingLineChart from "../components/SpendingLineChart";
import CategoryDonut from "../components/CategoryDonut";
import CategoryTable from "../components/CategoryTable";
import InsightsPanel from "../components/InsightsPanel";

export default function OverviewSection() {
  const [mode, setMode] = useState("month");
  const [date, setDate] = useState(new Date());

  function shiftDate(direction) {
    setDate((d) => {
      if (mode === "month") {
        return new Date(d.getFullYear(), d.getMonth() + direction, 1);
      }
      return new Date(d.getFullYear(), d.getMonth(), d.getDate() + direction);
    });
  }

  const { data, groups, loading, error } = useDashboardData({ mode, date });

  const [drawerOpen, setDrawerOpen] = useState(false);
  const [activeCategory, setActiveCategory] = useState(null);
  const [assignOpen, setAssignOpen] = useState(false);
  const [incomeOpen, setIncomeOpen] = useState(false);

  const flatCategories = useMemo(() => {
    if (!groups?.length) return data?.categories ?? [];
    return groups.flatMap((g) => g.items);
  }, [groups, data]);

  function openCategory(cat) {
    setActiveCategory(cat);
    setDrawerOpen(true);
  }

  if (loading) return <div className="text-white/70">Loading dashboard…</div>;
  if (error) return <div className="text-red-200">{error}</div>;
  if (!data) return null;
  const remaining =
  data.totals.income - data.totals.assigned;

  return (
    <>
      {/* HEADER */}
      <motion.div
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
        className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between"
      >
        <div>
          <div className="text-2xl font-extrabold tracking-tight">
            Dashboard <span className="text-lime-200">Overview</span>
          </div>
          <div className="mt-1 text-sm text-white/65">
            Monthly + daily insights with category-level details
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <ModeToggle mode={mode} setMode={setMode} />

          <DatePill
            date={date}
            mode={mode}
            onPrev={() => shiftDate(-1)}
            onNext={() => shiftDate(1)}
          />

          <button
            onClick={() => setAssignOpen(true)}
            className="rounded-2xl bg-lime-300 px-4 py-2 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition"
          >
            + Assign money
          </button>
          <button
            onClick={() => setIncomeOpen(true)}
            className="rounded-2xl bg-white/10 px-4 py-2 text-sm ring-1 ring-white/15 hover:bg-white/15"
          >
            + Add income
          </button>
        </div>
      </motion.div>

      {/* SUMMARY */}
      <div className="mt-7">
        <SummaryCards totals={data.totals} mode={mode} />
      </div>

      {/* MAIN GRID */}
      <div className="mt-7 grid gap-6 lg:grid-cols-12">
        <div className="lg:col-span-8 space-y-6">
          <SpendingLineChart series={data.series} mode={mode} />

          <div className="grid gap-6 md:grid-cols-2">
            <CategoryDonut categories={flatCategories} onInfo={openCategory} />
            <InsightsPanel totals={data.totals} mode={mode} />
          </div>
        </div>

        <div className="lg:col-span-4 space-y-6">
          <CategoryTable categories={flatCategories} onInfo={openCategory} />
        </div>
      </div>

      {/* DRAWERS */}
      <CategoryDrawer
        open={drawerOpen}
        category={activeCategory}
        month={date.getMonth() + 1}
        year={date.getFullYear()}
        onClose={() => setDrawerOpen(false)}
      />

      <AssignMoneyModal
        open={assignOpen}
        onClose={() => setAssignOpen(false)}
        categories={flatCategories}
        date={date}
        remaining={data.totals.available}
      />

      <SetIncomeModal
        open={incomeOpen}
        onClose={() => setIncomeOpen(false)}
        date={date}
      />
    </>
  );
}

/* ---------- helpers ---------- */

function ModeToggle({ mode, setMode }) {
  return (
    <div className="flex overflow-hidden rounded-2xl bg-white/10 ring-1 ring-white/15">
      {["month", "day"].map((m) => (
        <button
          key={m}
          onClick={() => setMode(m)}
          className={[
            "px-4 py-2 text-sm transition",
            mode === m
              ? "bg-lime-300 text-[#061a12] font-semibold"
              : "text-white/75 hover:bg-white/10",
          ].join(" ")}
        >
          {m[0].toUpperCase() + m.slice(1)}
        </button>
      ))}
    </div>
  );
}

function DatePill({ date, onPrev, onNext, mode }) {
  const label =
    mode === "month"
      ? date.toLocaleString(undefined, { month: "long", year: "numeric" })
      : date.toLocaleDateString(undefined, {
          weekday: "short",
          month: "short",
          day: "numeric",
        });

  return (
    <div className="flex items-center gap-2 rounded-2xl bg-white/10 px-3 py-2 ring-1 ring-white/15">
      <button onClick={onPrev} className="h-8 w-8 rounded-xl hover:bg-white/10">
        ‹
      </button>
      <div className="min-w-[180px] text-center text-sm">{label}</div>
      <button onClick={onNext} className="h-8 w-8 rounded-xl hover:bg-white/10">
        ›
      </button>
    </div>
  );
}
