// frontend/src/pages/dashboard/family/FamilyHome.jsx
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { familyApi } from "../../../api/familyApi";
import PendingInvites from "./PendingInvites";

export default function FamilyHome() {
  const navigate = useNavigate();

  const [families, setFamilies] = useState([]);
  const [name, setName] = useState("");
  const [creating, setCreating] = useState(false);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  async function load() {
    setErr("");
    setLoading(true);
    try {
      const res = await familyApi.getMyFamilies(); // GET /api/family/my
      setFamilies(Array.isArray(res) ? res : []);
    } catch (e) {
      setErr(e?.message || "Failed to load families");
      setFamilies([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  const canCreate = useMemo(() => name.trim().length >= 2, [name]);

  async function createFamily() {
    if (!canCreate || creating) return;
    setErr("");
    setCreating(true);
    try {
      await familyApi.createFamily({ name: name.trim() }); // POST /api/family/create
      setName("");
      await load();
    } catch (e) {
      setErr(e?.message || "Create failed");
    } finally {
      setCreating(false);
    }
  }

  return (
    <div className="space-y-8">
      <Header title="Family" subtitle="Shared budgets & members" />

      {err ? <Banner text={err} /> : null}

      <SectionCard title="👨‍👩‍👧 My Families">
        {loading ? (
          <Muted>Loading…</Muted>
        ) : families.length === 0 ? (
          <Muted>No families yet.</Muted>
        ) : (
          <div className="space-y-2">
            {families.map((f) => (
              <FamilyRow
                key={f.familyId ?? f.id}
                family={f}
                onOpen={(id) => navigate(`/dashboard/family/${id}`)}
              />
            ))}
          </div>
        )}
      </SectionCard>

      <SectionCard title="📩 Invitations">
        <PendingInvites onChange={load} />
      </SectionCard>

      <SectionCard title="➕ Create Family">
        <CreateFamilyForm
          name={name}
          setName={setName}
          canCreate={canCreate}
          creating={creating}
          onCreate={createFamily}
        />
        <div className="text-xs text-white/50">
          Creator becomes <span className="text-white/70">OWNER</span> automatically.
        </div>
      </SectionCard>
    </div>
  );
}

/* ---------------- components ---------------- */

function FamilyRow({ family, onOpen }) {
  const familyId = family.familyId ?? family.id;

  // tolerant label: avoid gray empty rows
  const label =
    family.name ||
    family.familyName ||
    family.groupName ||
    `Family #${familyId}`;

  const role = family.role || family.familyRole;

  return (
    <div className="flex items-center justify-between rounded-xl bg-white/5 px-4 py-3 ring-1 ring-white/10">
      <div className="min-w-0">
        <div className="font-medium truncate">{label}</div>
        {role ? <div className="text-xs text-white/60">{role}</div> : null}
      </div>

      <button
        onClick={() => onOpen(familyId)}
        className="rounded-lg bg-lime-300 px-3 py-1 text-sm font-semibold text-black hover:bg-lime-200 transition"
      >
        Open
      </button>
    </div>
  );
}

function CreateFamilyForm({ name, setName, canCreate, creating, onCreate }) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Family name (min 2 chars)"
        className="flex-1 rounded-xl bg-black/30 px-3 py-2 ring-1 ring-white/15 outline-none"
      />
      <button
        onClick={onCreate}
        disabled={!canCreate || creating}
        className="rounded-xl px-4 py-2 font-semibold text-black transition
                   bg-lime-400 hover:bg-lime-300 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {creating ? "Creating…" : "Create"}
      </button>
    </div>
  );
}

/* ---------------- shared ui ---------------- */

function Header({ title, subtitle }) {
  return (
    <div>
      <div className="text-2xl font-extrabold">{title}</div>
      <div className="text-sm text-white/60">{subtitle}</div>
    </div>
  );
}

function SectionCard({ title, children }) {
  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
      <div className="text-sm font-semibold">{title}</div>
      {children}
    </div>
  );
}

function Muted({ children }) {
  return <div className="text-sm text-white/60">{children}</div>;
}

function Banner({ text }) {
  return (
    <div className="rounded-2xl bg-red-500/10 px-4 py-3 ring-1 ring-red-400/20 text-sm text-red-200">
      {text}
    </div>
  );
}
