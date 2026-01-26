import { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext(null);

function parseUser(token) {
  try {
    return jwtDecode(token); // must contain userId, email, etc.
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() =>
    localStorage.getItem("token")
  );
  const [loading, setLoading] = useState(true);

  const user = token ? parseUser(token) : null;
  const isAuthenticated = !!token;

  useEffect(() => {
    setLoading(false);
  }, []);

  function login(newToken, remember = true) {
    setToken(newToken);
    if (remember) localStorage.setItem("token", newToken);
  }

  function logout() {
    setToken(null);
    localStorage.removeItem("token");
  }

  return (
    <AuthContext.Provider
      value={{
        token,
        user,               // ✅ NEW
        isAuthenticated,
        login,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
