import { useAuth } from "../../context/AuthContext";
import { Link } from "react-router-dom";

const pageStyle = { maxWidth: 880, margin: "40px auto", padding: "0 20px" };
const headerStyle = { display: "flex", justifyContent: "space-between", alignItems: "center" };
const mutedStyle = { color: "#666" };
const sectionStyle = { marginTop: 24 };
const hintStyle = { color: "#999" };

export default function AdminPage() {
  const { user } = useAuth();

  return (
    <div style={pageStyle}>
      <div style={headerStyle}>
        <h1>🛠️ Admin Console</h1>
        <Link to="/dashboard">← Back to app</Link>
      </div>

      <p style={mutedStyle}>
        Signed in as <strong>{user?.sub}</strong> · role <strong>{user?.role}</strong>
      </p>

      <hr />

      <section style={sectionStyle}>
        <h2>What lives here (coming next)</h2>
        <ul>
          <li>👥 User management — list users, change roles (USER / ADMIN / SUPPORT)</li>
          <li>🏦 Mock bank control — fund / inspect bKash·Nagad·DBBL balances</li>
          <li>📊 System health — counts, recent signups, error feed</li>
          <li>🧾 Audit log — who did what, when</li>
        </ul>
        <p style={hintStyle}>
          Only reachable with an <strong>ADMIN</strong> token. A USER who types <code>/admin</code> is redirected back to the dashboard.
        </p>
      </section>
    </div>
  );
}