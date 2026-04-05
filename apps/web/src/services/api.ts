import axios from "axios";
import { clearSession, getSession } from "./auth";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
});

api.interceptors.request.use((config) => {
  const session = getSession();

  if (session?.token && !config.headers?.Authorization) {
    config.headers.Authorization = `Bearer ${session.token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status as number | undefined;
    const requestUrl = String(error.config?.url ?? "");
    const isAuthRequest = requestUrl.startsWith("/api/auth/");

    if (!isAuthRequest && (status === 401 || status === 403)) {
      clearSession();
      if (window.location.pathname !== "/login") {
        window.location.assign("/login");
      }
    }

    return Promise.reject(error);
  },
);

export default api;
