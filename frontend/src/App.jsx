import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";

import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import VerifyEmailPage from "./pages/VerifyEmailPage";
import VerifyEmailInfo from "./pages/VerifyEmailInfo";
import DashboardPage from "./pages/dashboard/DashboardPage";
import ProtectedRoute from "./components/ProtectedRoute";
import OnboardingPage from "./pages/onboarding/OnboardingPage";
import DashboardLayout from "./pages/dashboard/DashboardLayout";
import OverviewSection from "./pages/dashboard/sections/OverviewSection";
import DailyAnalytics from "./pages/dashboard/sections/DailyAnalytics";
import MonthlyAnalytics from "./pages/dashboard/sections/MonthlyAnalytics";
import FamilyDashboard from "./pages/dashboard/family/FamilyDashboard";
import BankingDashboard from "./pages/dashboard/sections/BankingDashboard";
import RecurringBillsDashboard from "./pages/dashboard/sections/RecurringBillsDashboard";
import FamilyHome from "./pages/dashboard/family/FamilyHome";
import ProfilePage from "./pages/dashboard/profile/ProfilePage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/verify-email" element={<VerifyEmailPage />} />
          <Route path="/verify-email-info" element={<VerifyEmailInfo />} />
          <Route
            path="/onboarding"
            element={
              <ProtectedRoute>
                <OnboardingPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            {/* DEFAULT */}
            <Route index element={<OverviewSection />} />

            {/* ANALYTICS */}
            <Route path="daily" element={<DailyAnalytics />} />
            <Route path="monthly" element={<MonthlyAnalytics />} />
            <Route path="profile" element={<ProfilePage />} />

            {/* OTHER PAGES */}
            <Route path="family" element={<FamilyHome />} />
            <Route path="family/:familyId" element={<FamilyDashboard />} />    
             <Route path="banking" element={<BankingDashboard />} />
            <Route path="subscriptions" element={<RecurringBillsDashboard />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
