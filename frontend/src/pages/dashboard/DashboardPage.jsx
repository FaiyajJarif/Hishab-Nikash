import { Routes, Route } from "react-router-dom";
import DashboardLayout from "./DashboardLayout";

import OverviewSection from "./sections/OverviewSection";
import DailyAnalytics from "./sections/DailyAnalytics";
import MonthlyAnalytics from "./sections/MonthlyAnalytics";
import FamilyDashboard from "./family/FamilyDashboard";
import FamilyHome from "./family/FamilyHome";
import BankingDashboard from "./sections/BankingDashboard";
import RecurringBillsDashboard from "./sections/RecurringBillsDashboard";
import ProfilePage from "./profile/ProfilePage";
import ProtectedRoute from "../../components/ProtectedRoute";

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <DashboardLayout>
        <Routes>
          <Route index element={<OverviewSection />} />
          <Route path="daily" element={<DailyAnalytics />} />
          <Route path="monthly" element={<MonthlyAnalytics />} />

          {/* ✅ PROFILE */}
          <Route path="profile" element={<ProfilePage />} />

          {/* ✅ FAMILY */}
          <Route path="family" element={<FamilyHome />} />
          <Route path="family/:familyId" element={<FamilyDashboard />} />

          <Route path="banking" element={<BankingDashboard />} />
          <Route path="subscriptions" element={<RecurringBillsDashboard />} />
        </Routes>
      </DashboardLayout>
    </ProtectedRoute>
  );
}
