import { useState } from "react";
import { familyApi } from "../../../api/familyApi";

export default function FamilyInvites({ familyId, onInviteSent }) {
  const [email, setEmail] = useState("");
  const [msg, setMsg] = useState("");
  const [sending, setSending] = useState(false);

  async function sendInvite() {
    setMsg("");
    if (!email.trim()) return setMsg("Enter email");

    setSending(true);
    try {
      await familyApi.inviteMember(familyId, {
        email: email.trim(),
      });

      setMsg("Invite sent ✅");
      setEmail("");
      onInviteSent?.();
    } catch (e) {
      setMsg(e?.message || "Invite failed");
    } finally {
      setSending(false);
    }
  }

  return (
    <Card>
      <div className="text-sm font-semibold text-white/85 mb-3">
        ✉️ Invite Member
      </div>

      <div className="space-y-3">
        <input
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="member@email.com"
          className="w-full rounded-2xl bg-white/10 px-4 py-3 text-sm ring-1 ring-white/15 outline-none"
        />

        <button
          onClick={sendInvite}
          disabled={sending}
          className="rounded-2xl bg-lime-300 px-4 py-3 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition disabled:opacity-60"
        >
          {sending ? "Sending…" : "Invite"}
        </button>

        {msg && <div className="text-sm text-white/75">{msg}</div>}

        <div className="text-xs text-white/55">
          Invites are sent by <span className="text-white/70">email</span>.  
          The user accepts it from their Invitations.
        </div>
      </div>
    </Card>
  );
}

function Card({ children }) {
  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
      {children}
    </div>
  );
}
