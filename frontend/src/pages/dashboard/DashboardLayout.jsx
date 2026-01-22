import { Outlet } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import TopBar from "./components/TopBar";

export default function DashboardLayout() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-[#071b13] via-[#061a12] to-[#04110c] text-white flex">
      {/* LEFT SIDEBAR */}
      <Sidebar />

      {/* MAIN CONTENT */}
      <div className="flex-1 flex flex-col">
        <TopBar />
        <main className="flex-1 px-4 py-6 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
