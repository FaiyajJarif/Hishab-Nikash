import { useEffect, useState } from "react";
import { familyApi } from "../../../api/familyApi";

export default function PendingInvites({ onChange }) {
  const [invites, setInvites] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    const res = await familyApi.getMyInvitations();
    setInvites(res || []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, []);

  async function accept(token) {
    await familyApi.acceptInvitation(token);
    await load();
    onChange?.();
  }

  async function reject(token) {
    await familyApi.rejectInvitation(token);
    await load();
  }

  if (loading) {
    return <Muted>Loading invitations…</Muted>;
  }

  if (!invites.length) {
    return <Muted>No pending invitations.</Muted>;
  }

  return (
    <div className="space-y-2">
      {invites.map(inv => (
        <div
          key={inv.token}
          className="flex items-center justify-between rounded-xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
        >
          <div>
            <div className="text-sm font-medium">
              {inv.familyName}
            </div>
            <div className="text-xs text-white/60">
              Invited as {inv.role}
            </div>
          </div>

          <div className="flex gap-2">
            <button
              onClick={() => accept(inv.token)}
              className="rounded-lg bg-lime-300 px-3 py-1 text-sm font-semibold text-black"
            >
              Accept
            </button>
            <button
              onClick={() => reject(inv.token)}
              className="rounded-lg bg-white/10 px-3 py-1 text-sm"
            >
              Reject
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}

function Muted({ children }) {
  return <div className="text-sm text-white/60">{children}</div>;
}
