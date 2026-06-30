import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ children, roles }) {
  const { token, user, loading } = useAuth();

  // wait until the initial auth check settles
  if (loading) return null;

  // 1) not logged in -> go to login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // 2) logged in but role not allowed -> bounce to the normal dashboard
  if (roles && roles.length > 0) {
    const role = user?.role;
    if (!role || !roles.includes(role)) {
      return <Navigate to="/dashboard" replace />;
    }
  }

  // 3) allowed
  return children;
}