const API_ROOT = "/api";

export async function api(path, options = {}) {
  const config = {
    method: options.method || "GET",
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...(options.headers || {})
    },
    ...(options.body ? { body: JSON.stringify(options.body) } : {})
  };

  let response;
  try {
    response = await fetch(`${API_ROOT}${path}`, config);
  } catch {
    throw new Error("Cannot connect to the backend. Make sure Spring Boot is running on http://localhost:8080.");
  }

  const text = await response.text();
  let payload = null;
  if (text) {
    try { payload = JSON.parse(text); }
    catch { payload = text; }
  }

  if (!response.ok) {
    const message = payload?.message || payload?.error || (typeof payload === "string" ? payload : "");
    throw new Error(message || `Request failed (${response.status})`);
  }

  if (payload && typeof payload === "object" && "success" in payload) {
    if (!payload.success) throw new Error(payload.message || "The operation was not successful.");
    return payload.data;
  }

  return payload;
}

export const get = (path) => api(path);
export const post = (path, body) => api(path, { method: "POST", body });
export const put = (path, body) => api(path, { method: "PUT", body });
export const putAction = (path) => api(path, { method: "PUT" });
export const del = (path) => api(path, { method: "DELETE" });

export function queryString(values) {
  return new URLSearchParams(values).toString();
}
