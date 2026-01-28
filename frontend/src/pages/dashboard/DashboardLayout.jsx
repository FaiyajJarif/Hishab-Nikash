import { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import TopBar from "./components/TopBar";
import { useAlertStomp } from "../../hooks/useAlertStomp";

export default function DashboardLayout() {
  const [alerts, setAlerts] = useState([]);

  useAlertStomp((alert) => {
    console.log("📥 WS ALERT RECEIVED", alert);
    setAlerts(prev => [alert, ...prev]);
  });

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#071b13] via-[#061a12] to-[#04110c] text-white flex">
      <Sidebar />

      <div className="flex-1 flex flex-col">
        <TopBar alerts={alerts} />
        <main className="flex-1 px-4 py-6 overflow-y-auto">
          <Outlet context={{ alerts, setAlerts }} />
        </main>
      </div>
    </div>
  );
}
