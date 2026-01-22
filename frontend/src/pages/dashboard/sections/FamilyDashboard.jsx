import { useEffect, useState } from "react";
import { dashboardApi } from "../api/dashboardApi";

export default function FamilyDashboard() {
  const [data, setData] = useState(null);
  const [email, setEmail] = useState("");
  const [msg, setMsg] = useState("");

  useEffect(() => {
    dashboardApi.getFamilyDashboard().then(setData).catch(() => setData(null));
  }, []);

  async function invite() {
    setMsg("");
    if (!email) return setMsg("Enter email");
    try {
      await dashboardApi.sendFamilyInvite({ email });
      setMsg("Invite sent ✅");
      setEmail("");
    } catch (e) {
      setMsg(e?.message || "Invite failed");
    }
  }

  return (
    <div className="space-y-6">
      <Header title="Family" subtitle="Invite members, view roles, see activity." />

      <Card>
        <div className="text-sm font-semibold text-white/85">Send invitation</div>
        <div className="mt-3 flex flex-col gap-3 sm:flex-row">
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="member@email.com"
            className="flex-1 rounded-2xl bg-white/10 px-4 py-3 text-sm ring-1 ring-white/15 outline-none"
          />
          <button
            onClick={invite}
            className="rounded-2xl bg-lime-300 px-4 py-3 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition"
          >
            Invite
          </button>
        </div>
        {msg ? <div className="mt-3 text-sm text-white/75">{msg}</div> : null}
      </Card>

      <Card>
        <div className="text-sm font-semibold text-white/85">Members</div>
        <div className="mt-3 space-y-2">
          {(data?.members || []).map((m) => (
            <div key={m.email} className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
              <div>
                <div className="text-sm text-white/85">{m.name || m.email}</div>
                <div className="text-xs text-white/60">{m.role || "Member"}</div>
              </div>
              <div className="text-xs text-white/60">{m.status || "Active"}</div>
            </div>
          ))}
          {!data?.members?.length ? <div className="text-sm text-white/60">No members yet.</div> : null}
        </div>
      </Card>

      <Card>
        <div className="text-sm font-semibold text-white/85">Invitations</div>
        <div className="mt-3 space-y-2">
          {(data?.invites || []).map((i) => (
            <div key={i.email} className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
              <div className="text-sm text-white/80">{i.email}</div>
              <div className="text-xs text-white/60">{i.status || "Pending"}</div>
            </div>
          ))}
          {!data?.invites?.length ? <div className="text-sm text-white/60">No pending invites.</div> : null}
        </div>
      </Card>
    </div>
  );
}

function Header({ title, subtitle }) {
  return (
    <div>
      <div className="text-2xl font-extrabold">{title}</div>
      <div className="mt-1 text-sm text-white/65">{subtitle}</div>
    </div>
  );
}
function Card({ children }) {
  return <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">{children}</div>;
}
