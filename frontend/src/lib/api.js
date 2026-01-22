const API_BASE = "http://localhost:8080";

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem("token");

  const res = await fetch(API_BASE + path, {
    method: options.method || "GET",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    throw {
      status: res.status,
      message:
        data?.error?.message ||
        data?.message ||
        "Request failed",
      raw: data,
    };
  }  

  return data;
}
