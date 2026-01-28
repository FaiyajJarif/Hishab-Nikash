import { useEffect, useState } from "react";
import { profileApi } from "../../../api/profileApi";
import { analyticsApi } from "../../../api/analyticsApi";
import { useOutletContext } from "react-router-dom";

import ProfileHeader from "./ProfileHeader";
import BudgetSnapshot from "./BudgetSnapshot";
import PeerComparisonCard from "./PeerComparisonCard";
import TrendCard from "./TrendCard";
import AlertsCard from "./AlertsCard";
import MonthSwitcher from "../family/MonthSwitcher";
import CategoryDonut from "./CategoryDonut";
import CashflowChart from "./CashflowChart";
import { Skeleton } from "../components/Skeleton";

export default function ProfilePage() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());

  const { alerts, setAlerts } = useOutletContext();

  const [data, setData] = useState(null);
  const [cashflow, setCashflow] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  async function load() {
    setLoading(true);
    try {
      const res = await profileApi.overview(month, year);
      setData(res);
    } catch (e) {
      setErr(e?.message || "Failed to load profile");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [month, year]);

  useEffect(() => {
    if (data?.latestAlerts) {
      setAlerts(data.latestAlerts);
    }
  }, [data]);

  useEffect(() => {
    profileApi.categoryDonut(month, year).then(setCategories);
  }, [month, year]);

  useEffect(() => {
    analyticsApi.cashflow(6).then(setCashflow);
  }, []);

  if (loading) return <Skeleton />;

  if (err) return <Banner text={err} onRetry={load} />;

  return (
    <div className="space-y-8">
      <ProfileHeader user={data.user} persona={data.persona} />

      <MonthSwitcher month={month} year={year} onChange={(m, y) => {
        setMonth(m);
        setYear(y);
      }} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <BudgetSnapshot summary={data.budgetSummary} month={month} year={year} />
        </div>
        <PeerComparisonCard data={data.peerComparison} />
      </div>

      <TrendCard data={data.trendDelta} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <CashflowChart data={cashflow} />
        <CategoryDonut data={categories} />
      </div>

      <AlertsCard alerts={alerts} />
    </div>
  );
}


/* ---- tiny ui ---- */

function Banner({ text, onRetry }) {
  return (
    <div className="rounded-2xl bg-red-500/10 px-4 py-3 ring-1 ring-red-400/20 text-sm text-red-200 flex items-center justify-between">
      <div>{text}</div>
      <button
        onClick={onRetry}
        className="rounded-lg bg-white/10 px-3 py-1 text-xs hover:bg-white/20"
      >
        Retry
      </button>
    </div>
  );
}
