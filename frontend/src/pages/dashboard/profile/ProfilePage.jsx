import { useEffect, useState } from "react";
import { profileApi } from "../../../api/profileApi";

import ProfileHeader from "./ProfileHeader";
import BudgetSnapshot from "./BudgetSnapshot";
import PeerComparisonCard from "./PeerComparisonCard";
import TrendCard from "./TrendCard";
import AlertsCard from "./AlertsCard";

export default function ProgilePage() {
  const now = new Date();
  const [month] = useState(now.getMonth() + 1);
  const [year] = useState(now.getFullYear());

  const [data, setData] = useState(null);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(true);

  async function load() {
    setErr("");
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

  if (loading) return <div className="text-white/60">Loading profile…</div>;
  if (err) return <Banner text={err} onRetry={load} />;
  if (!data) return <div className="text-white/60">No profile data.</div>;

  return (
    <div className="space-y-8">
      <ProfileHeader user={data.user} persona={data.persona} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <BudgetSnapshot summary={data.budgetSummary} month={month} year={year} />
        </div>
        <PeerComparisonCard data={data.peerComparison} />
      </div>

      <TrendCard data={data.trendDelta} />

      <AlertsCard alerts={data.latestAlerts} />
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
