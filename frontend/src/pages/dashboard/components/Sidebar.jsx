import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";

export default function Sidebar() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  return (
    <aside className="hidden w-72 shrink-0 border-r border-white/10 bg-[#061a12] px-5 py-6 md:block">
      <div className="flex items-center gap-3">
        <div className="grid h-10 w-10 place-items-center rounded-xl bg-lime-300/15 ring-1 ring-lime-200/20">
          <LogoMark />
        </div>
        <div className="text-sm font-semibold tracking-wide">
          Hishab <span className="text-lime-200">Nikash</span>
        </div>
      </div>

      <div className="mt-8 space-y-2">
        <NavItem to="/dashboard" end>Overview</NavItem>
        <NavItem to="/dashboard/daily">Daily analytics</NavItem>
        <NavItem to="/dashboard/monthly">Monthly analytics</NavItem>

        <div className="my-4 h-px bg-white/10" />

        <NavItem to="/dashboard/profile">Profile</NavItem>
        <NavItem to="/dashboard/family">Family</NavItem>
        <NavItem to="/dashboard/banking">Banking</NavItem>
        <NavItem to="/dashboard/subscriptions">Subscriptions</NavItem>
      </div>

      <button
        onClick={() => {
          logout?.();
          navigate("/login");
        }}
        className="mt-8 w-full rounded-2xl bg-white/10 px-4 py-3 text-sm ring-1 ring-white/15 hover:bg-white/15 transition"
      >
        Logout
      </button>
    </aside>
  );
}

function NavItem({ to, children, end = false }) {
    return (
      <NavLink
        to={to}
        end={end}
        className={({ isActive }) =>
          [
            "block rounded-2xl px-4 py-3 text-sm transition",
            isActive
              ? "bg-lime-300 text-[#061a12] font-semibold"
              : "text-white/75 hover:bg-white/10",
          ].join(" ")
        }
      >
        {children}
      </NavLink>
    );
  }  

function LogoMark() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M12 2l4 4-4 4-4-4 4-4Z" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
      <path d="M6 12l6 10 6-10" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
    </svg>
  );
}
