import { useMemo, useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useDashboardData } from "../hooks/useDashboardData";
import { useNavigate } from "react-router-dom";
import { familyApi } from "../../../api/familyApi";

import CategoryDrawer from "../components/CategoryDrawer";
import AssignMoneyModal from "../components/AssignMoneyModal";
import SetIncomeModal from "../components/SetIncomeModal";

import SummaryCards from "../components/SummaryCards";
import SpendingLineChart from "../components/SpendingLineChart";
import CategoryDonut from "../components/CategoryDonut";
import CategoryTable from "../components/CategoryTable";
import InsightsPanel from "../components/InsightsPanel";
import SpendingIncomeChart from "../charts/SpendingIncomeChart";
import CategoryGroup from "../components/CategoryGroup";

import RecurringBillsCard from "../components/RecurringBillsCard";
import AddRecurringBillModal from "../components/AddRecurringBillModal";

import { useAuth } from "../../../context/AuthContext";
import { connectUserSocket, disconnectUserSocket  } from "../../../ws/userSocket";

export default function OverviewSection() {
  const [mode, setMode] = useState("month");
  const [date, setDate] = useState(new Date());
  const [chartExpanded, setChartExpanded] = useState(false);
  const [days, setDays] = useState(14);
  const [billModalOpen, setBillModalOpen] = useState(false);

  const { user } = useAuth();

  const navigate = useNavigate();

  useEffect(() => {
    if (!user?.id) return;
  
    connectUserSocket(
      user.id,
      handleUserEvent,
      (status) => console.log("USER WS:", status)
    );
  
    return () => disconnectUserSocket();
  }, [user?.id]);
  const { data, groups, loading, error, reload } =
  useDashboardData({ mode, date });
  
  function handleUserEvent(event) {
    console.log("WS EVENT:", event);
  
    if (event.billId && event.amount) {
      window.dispatchEvent(new Event("dashboard-refresh"));
    }
  }  
  useEffect(() => {
    const refresh = () => reload();
    window.addEventListener("dashboard-refresh", refresh);
    return () => window.removeEventListener("dashboard-refresh", refresh);
  }, [reload]);   
   
  const [families, setFamilies] = useState([]);
  const [familiesLoading, setFamiliesLoading] = useState(true);
  useEffect(() => {
    familyApi
      .getMyFamilies()
      .then(setFamilies)
      .finally(() => setFamiliesLoading(false));
  }, []);
  function shiftDate(direction) {
    setDate((d) => {
      if (mode === "month") {
        return new Date(d.getFullYear(), d.getMonth() + direction, 1);
      }
      return new Date(d.getFullYear(), d.getMonth(), d.getDate() + direction);
    });
  }

  const [drawerOpen, setDrawerOpen] = useState(false);
  const [activeCategory, setActiveCategory] = useState(null);
  const [assignOpen, setAssignOpen] = useState(false);
  const [incomeOpen, setIncomeOpen] = useState(false);

  const groupedCategories = groups ?? [];
  const flatCategories = useMemo(
    () => groupedCategories.flatMap(g => g.items),
    [groupedCategories]
  );

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
        <div className="lg:col-span-5 space-y-6">
        <motion.div
          layout
          className="overflow-hidden rounded-3xl bg-white/10 ring-1 ring-white/15"
        >
          <motion.div
            layout
            animate={{ height: chartExpanded ? 360 : 200 }}
            transition={{ type: "spring", stiffness: 260, damping: 26 }}
            className="cursor-pointer"
            onClick={() => setChartExpanded((v) => !v)}
          >
            <div className="px-4 pt-4">
          <div className="flex gap-2 mb-2">
            <button
              onClick={() => setDays(7)}
              className={`px-3 py-1 text-xs rounded-lg ring-1 ${
                days === 7
                  ? "bg-lime-300 text-[#061a12]"
                  : "bg-white/10 text-white/70"
              }`}
            >
              7 days
            </button>

            <button
              onClick={() => setDays(14)}
              className={`px-3 py-1 text-xs rounded-lg ring-1 ${
                days === 14
                  ? "bg-lime-300 text-[#061a12]"
                  : "bg-white/10 text-white/70"
              }`}
            >
              14 days
            </button>
          </div>
        </div>

        <div className="flex justify-end gap-2 px-4 pt-3">
        {[7, 14].map((d) => (
          <button
            key={d}
            onClick={() => setDays(d)}
            className={[
              "rounded-xl px-3 py-1 text-xs ring-1 transition",
              days === d
                ? "bg-lime-300 text-[#061a12] ring-lime-300"
                : "bg-white/10 text-white/70 ring-white/15 hover:bg-white/15",
            ].join(" ")}
          >
            Last {d} days
          </button>
        ))}
      </div>

      <SpendingIncomeChart days={days} />
          </motion.div>

          <div className="px-4 pb-3 text-xs text-white/50 text-center">
            {chartExpanded ? "Click to collapse ▲" : "Click to expand ▼"}
          </div>
        </motion.div>

        <div className="space-y-6">
          <CategoryDonut categories={flatCategories} onInfo={openCategory} />
          <InsightsPanel totals={data.totals} mode={mode} />
        </div>
        <RecurringBillsCard categories={flatCategories} />

        <button
          onClick={() => setBillModalOpen(true)}
          className="mt-3 rounded-2xl bg-white/10 px-4 py-2 text-sm"
        >
          + Add recurring bill
        </button>

        <AddRecurringBillModal
          open={billModalOpen}
          onClose={() => setBillModalOpen(false)}
          categories={flatCategories}
        />
        </div>

        <div className="lg:col-span-7 space-y-6">
        {groupedCategories.map(group => (
          <CategoryGroup
            key={group.key}
            title={group.title}
            categories={group.items}
            onInfo={openCategory}
            onAssign={() => setAssignOpen(true)}
            onView={() => navigate("/dashboard/transactions")}
          />
        ))}
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
