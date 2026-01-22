import { useNavigate } from "react-router-dom";

export default function TopBar() {
  const navigate = useNavigate();

  return (
    <div className="sticky top-0 z-20 border-b border-white/10 bg-[#061a12]/70 backdrop-blur-xl">
      <div className="flex flex-wrap items-center justify-between gap-3 px-4 py-4 md:px-8">
        <div className="md:hidden text-sm font-semibold">
          Hishab <span className="text-lime-200">Nikash</span>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => navigate("/dashboard/family")}
            className="rounded-xl bg-white/10 px-3 py-2 text-xs ring-1 ring-white/15 hover:bg-white/15 transition"
          >
            Family
          </button>

          <button
            onClick={() => navigate("/dashboard/profile")}
            className="rounded-xl bg-white/10 px-3 py-2 text-xs ring-1 ring-white/15 hover:bg-white/15 transition"
          >
            Profile
          </button>

          <button
            onClick={() => navigate("/dashboard")}
            className="rounded-xl bg-lime-300 px-3 py-2 text-xs font-semibold text-[#061a12] hover:bg-lime-200 transition"
          >
            + Add transaction
          </button>
        </div>
      </div>
    </div>
  );
}
