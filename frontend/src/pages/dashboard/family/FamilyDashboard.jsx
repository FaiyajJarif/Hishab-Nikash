import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";

import { connectFamilySocket, disconnectFamilySocket } from "../../../sockets/familySocket";
import { familyApi } from "../../../api/familyApi";

import FamilySummary from "./FamilySummary";
import FamilyCategories from "./FamilyCategories";
import FamilyActivity from "./FamilyActivity";
import FamilyMembers from "./FamilyMembers";
import FamilyInvites from "./FamilyInvites";
import MonthSwitcher from "./MonthSwitcher";
import FamilyExpenseHistory from "./FamilyExpenseHistory";
import FamilyExpenseSummary from "./FamilyExpenseSummary";

export default function FamilyDashboard() {
  const { familyId } = useParams();

  const now = new Date();

  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const isFutureMonth = new Date(year, month - 1) > new Date();

  const [summary, setSummary] = useState(null);
  const [activity, setActivity] = useState([]);
  const [myRole, setMyRole] = useState(null);
  const [membersCount, setMembersCount] = useState(0);
  const [wsStatus, setWsStatus] = useState("DISCONNECTED");
  const [expenseRefresh, setExpenseRefresh] = useState(0);

  // ✅ Permission: allow ADMIN + OWNER
  const canInvite = useMemo(() => {
    const r = (myRole || "").toUpperCase();
    return r === "ADMIN" || r === "OWNER";
  }, [myRole]);

  async function loadRoleAndCounts() {
    if (!familyId) return;

    // /api/family/my -> [{ familyId, role, userId ... }]
    const mine = await familyApi.getMyFamilies();
    const row = (mine || []).find((x) => String(x.familyId) === String(familyId));
    setMyRole(row?.role || null);

    // members count (optional but nice for header)
    const members = await familyApi.getMembers(familyId);
    setMembersCount(Array.isArray(members) ? members.length : 0);
  }

  async function refetchBudget() {
    if (!familyId) return;
    const res = await familyApi.getSummary(familyId, month, year);
    console.log("SUMMARY RESPONSE:", res);
    setSummary(res);
    

  }

  useEffect(() => {
    if (!familyId) return;

    // initial loads
    refetchBudget();
    loadRoleAndCounts();

    // socket
    connectFamilySocket(familyId, (event) => {
        setActivity(prev => [event, ...prev.slice(0, 19)]);
      
        if (
          event.type === "FAMILY_ROLE_CHANGED" ||
          event.type === "FAMILY_MEMBER_REMOVED" ||
          event.type === "FAMILY_MEMBER_JOINED"||
          event.type === "FAMILY_BUDGET_PLANNED" ||   // 👈 ADD
          event.type === "FAMILY_INCOME_UPDATED" ||
          event.type === "CATEGORY_RENAMED" ||
          event.type === "CATEGORY_DELETED" ||
          event.type === "FAMILY_EXPENSE_ADDED"
        ) {
          loadRoleAndCounts();
          refetchBudget();
        }
        if (event.type === "FAMILY_EXPENSE_ADDED") {
          setExpenseRefresh(x => x + 1);
        }        
      },
      setWsStatus
    );      

    return () => disconnectFamilySocket();
  }, [familyId, month, year]);

  if (!familyId) {
    return <div className="text-white/60">Select a family to view shared budget.</div>;
  }

  return (
    <div className="space-y-8">
      <Header
        title="Family Budget"
        subtitle={`Shared budget · ${month}/${year}`}
        myRole={myRole}
        membersCount={membersCount}
        wsStatus={wsStatus}
        />
        <MonthSwitcher
          month={month}
          year={year}
          onChange={(m, y) => {
            setMonth(m);
            setYear(y);
          }}
        />
        <FamilySummary
            summary={summary}
            month={month}
            year={year}
            canEdit={myRole === "ADMIN" || myRole === "EDITOR"}
            onSaveIncome={async (income) => {
              await familyApi.setIncome(familyId, { month, year, income });
              refetchBudget();
            }}disabled={isFutureMonth}
          />
          <FamilyExpenseSummary
  familyId={familyId}
  month={month}
  year={year}
/>
        <FamilyCategories
        familyId={familyId}
        month={month}
        year={year}
        summary={summary}
        canEdit={myRole === "ADMIN" || myRole === "EDITOR"}
        disabled={isFutureMonth}
        />

      <FamilyActivity activity={activity} />
      <FamilyExpenseHistory
        familyId={familyId}
        refreshKey={expenseRefresh}
      />

      {/* ✅ Members + Invites */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <FamilyMembers familyId={familyId} />

        {canInvite ? (
          <FamilyInvites
            familyId={familyId}
            onInviteSent={() => {
              // optional: refresh members after inviting
              loadRoleAndCounts();
            }}
          />
        ) : (
          <NoPermissionCard />
        )}
      </div>
    </div>
  );
}

function Header({ title, subtitle, myRole, membersCount, wsStatus }) {
    return (
      <div className="space-y-2">
        <div className="text-2xl font-extrabold">{title}</div>
  
        <div className="flex items-center justify-between flex-wrap gap-2">
          <div className="text-sm text-white/65">{subtitle}</div>
  
          {/* 👇 BADGES */}
          <div className="flex items-center gap-2">
            <span className="text-xs px-2 py-1 rounded-full bg-white/10">
              {membersCount} members
            </span>
  
            {myRole && (
              <span
                className={`text-xs px-2 py-1 rounded-full
                  ${myRole === "ADMIN"
                    ? "bg-lime-500/20 text-lime-300"
                    : myRole === "EDITOR"
                    ? "bg-blue-500/20 text-blue-300"
                    : "bg-white/10 text-white/60"}
                `}
              >
                {myRole}
              </span>
            )}
            {wsStatus && (
                <span
                    className={`text-xs px-2 py-1 rounded-full font-medium
                    ${
                        wsStatus === "CONNECTED"
                        ? "bg-lime-500/20 text-lime-300"
                        : "bg-white/10 text-white/50"
                    }
                    `}
                >
                    {wsStatus === "CONNECTED" ? "● Live" : "● Offline"}
                </span>
                )}
          </div>
        </div>
      </div>
    );
  }  

function NoPermissionCard() {
  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
      <div className="text-sm font-semibold text-white/85 mb-2">✉️ Invitations</div>
      <div className="text-sm text-white/60">
        You don’t have permission to invite members in this family.
      </div>
    </div>
  );
}
