import { useEffect, useMemo, useState } from "react";

export default function ProfileHeader({ user, persona }) {
    const name = user?.name || "User";
    const email = user?.email || "";
    const label = persona?.label || "—";
    const emoji = persona?.emoji || "💡";
    const desc = persona?.description || "No persona available yet.";
    const [open, setOpen] = useState(false);
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0">
            <div className="text-2xl font-extrabold truncate">{name}</div>
            <div className="text-sm text-white/60 truncate">{email}</div>
          </div>
  
          <div
            onClick={() => setOpen(true)}
            className="rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10 text-right cursor-pointer"
            >
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
        {open && (
            <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center">
                <div className="rounded-3xl bg-[#061a12] p-6 w-full max-w-md ring-1 ring-white/15">
                <div className="text-lg font-bold mb-2">
                    {emoji} {label}
                </div>

                <p className="text-sm text-white/70">{desc}</p>

                <div className="mt-4 text-xs text-white/55">
                    This persona is calculated based on how much money you keep unspent
                    relative to your income.
                </div>

                <button
                    onClick={() => setOpen(false)}
                    className="mt-6 w-full rounded-xl bg-white/10 py-2 hover:bg-white/20"
                >
                    Close
                </button>
                </div>
            </div>
            )}
      </div>
    );
  }
  