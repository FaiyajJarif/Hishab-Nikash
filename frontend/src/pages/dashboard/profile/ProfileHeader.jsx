export default function ProfileHeader({ user, persona }) {
    const name = user?.name || "User";
    const email = user?.email || "";
    const label = persona?.label || "—";
    const emoji = persona?.emoji || "💡";
    const desc = persona?.description || "No persona available yet.";
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0">
            <div className="text-2xl font-extrabold truncate">{name}</div>
            <div className="text-sm text-white/60 truncate">{email}</div>
          </div>
  
          <div className="rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10 text-right">
            <div className="text-xs text-white/55 uppercase tracking-wide">
              Financial Persona
            </div>
            <div className="mt-1 text-lg font-bold">
              {emoji} {label}
            </div>
            <div className="mt-1 text-xs text-white/60 max-w-[260px]">
              {desc}
            </div>
          </div>
        </div>
  
        <div className="text-sm text-white/60">
          This page summarizes your month: budget status, trends, peer comparison,
          and alerts.
        </div>
      </div>
    );
  }
  