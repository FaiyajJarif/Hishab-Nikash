import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { familyApi } from "../../../api/familyApi";
import { useAuth } from "../../../context/AuthContext";

export default function FamilyMembers({ familyId }) {
  const navigate = useNavigate();
  const { user } = useAuth();
  const myUserId = user?.userId;

  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    const res = await familyApi.getMembers(familyId);
    setMembers(res || []);
    setLoading(false);
  }

  useEffect(() => {
    if (!familyId) return;
    load();
  }, [familyId]);

  if (loading) {
    return <Muted>Loading members…</Muted>;
  }

  const me = members.find(m => m.userId === myUserId);
  const myRole = me?.role;
  console.log("MY ROLE:", myRole);

  const adminCount = members.filter(m => m.role === "ADMIN").length;
  const isAdmin = myRole === "ADMIN";

  async function removeMember(userId) {
    if (!confirm("Remove this member from family?")) return;
    await familyApi.removeMember(familyId, userId);
    load();
  }

  async function leaveFamily() {
    if (myRole === "ADMIN" && adminCount === 1) {
      alert("You are the last ADMIN. Assign another admin before leaving.");
      return;
    }
    if (!confirm("Leave this family?")) return;
    await familyApi.leaveFamily(familyId);
    navigate("/dashboard/family");
  }

  async function changeRole(userId, role) {
    try {
      await familyApi.changeRole(familyId, userId, role);
      load();
    } catch (e) {
      console.error(e);
      alert(e?.response?.data?.message || "Role change failed");
    }
  }
  

  return (
    <Card title="👥 Family Members">
      {members.map(m => {
        const isMe = m.userId === myUserId;
        const isLastAdmin = m.role === "ADMIN" && adminCount === 1;

        return (
          <div
            key={m.userId}
            className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
          >
            {/* LEFT */}
            <div>
              <div className="text-sm font-medium">
                {m.name || m.email}
                {isMe && " (You)"}
              </div>
              <div className="text-xs text-white/60">
                Role: {m.role}
              </div>
            </div>

            {/* RIGHT */}
            <div className="flex items-center gap-2">
              {/* Role selector (ADMIN only, not self) */}
              {isAdmin && !isMe && (
                <select
                  value={m.role}
                  onChange={(e) =>
                    changeRole(m.userId, e.target.value)
                  }
                  className="rounded-lg bg-black/30 px-2 py-1 text-xs ring-1 ring-white/10"
                >
                  <option value="ADMIN">ADMIN</option>
                  <option value="EDITOR">EDITOR</option>
                  <option value="VIEWER">VIEWER</option>
                </select>
              )}
              {/* Transfer admin (ADMIN only, not self, target not already ADMIN) */}
                {isAdmin && !isMe && m.role !== "ADMIN" && (
                <button
                    onClick={() => transferAdmin(m.userId)}
                    className="rounded-lg bg-yellow-400/20 px-3 py-1 text-xs
                            text-yellow-300 hover:bg-yellow-400/30"
                >
                    Make Admin
                </button>
                )}

              {/* Remove member (ADMIN only, not last admin) */}
              {isAdmin && !isMe && !isLastAdmin && (
                <button
                  onClick={() => removeMember(m.userId)}
                  className="rounded-lg bg-red-500/20 px-3 py-1 text-xs text-red-300 hover:bg-red-500/30"
                >
                  Remove
                </button>
              )}

              {/* Leave family (self) */}
              {isMe && (
                <button
                  onClick={leaveFamily}
                  className="rounded-lg bg-white/10 px-3 py-1 text-xs hover:bg-white/20"
                >
                  Leave
                </button>
              )}
            </div>
          </div>
        );
      })}
    </Card>
  );
}

/* ---------- UI helpers ---------- */

function Card({ title, children }) {
  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-3">
      <div className="text-sm font-semibold">{title}</div>
      {children}
    </div>
  );
}

function Muted({ children }) {
  return <div className="text-sm text-white/60">{children}</div>;
}

async function transferAdmin(targetUserId) {
    if (!confirm("Transfer admin role to this member?")) return;
  
    // promote target
    await familyApi.changeRole(familyId, targetUserId, "ADMIN");
  
    // demote self
    await familyApi.changeRole(familyId, myUserId, "EDITOR");
  
    load();
  }
  