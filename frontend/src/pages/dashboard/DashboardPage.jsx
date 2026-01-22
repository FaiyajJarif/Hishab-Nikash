import { Routes, Route, Navigate } from "react-router-dom";
import DashboardLayout from "./DashboardLayout";

import OverviewSection from "./sections/OverviewSection";
import DailyAnalytics from "./sections/DailyAnalytics";
import MonthlyAnalytics from "./sections/MonthlyAnalytics";
import ProfileDashboard from "./sections/ProfileDashboard";
import FamilyDashboard from "./sections/FamilyDashboard";
import BankingDashboard from "./sections/BankingDashboard";
import RecurringBillsDashboard from "./sections/RecurringBillsDashboard";

export default function DashboardPage() {
  return (
    <DashboardLayout>
      <Routes>
        <Route index element={<OverviewSection />} />
        <Route path="daily" element={<DailyAnalytics />} />
        <Route path="monthly" element={<MonthlyAnalytics />} />

        <Route path="profile" element={<ProfileDashboard />} />
        <Route path="family" element={<FamilyDashboard />} />
        <Route path="banking" element={<BankingDashboard />} />
        <Route path="subscriptions" element={<RecurringBillsDashboard />} />

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </DashboardLayout>
  );
}
