export interface AuthSession {
  token: string;
  role: string;
  userId: number;
  username: string;
}

const STORAGE_KEYS = {
  token: "token",
  role: "role",
  userId: "userId",
  username: "username",
} as const;

export function getSession(): AuthSession | null {
  const token = localStorage.getItem(STORAGE_KEYS.token);
  const role = localStorage.getItem(STORAGE_KEYS.role);
  const userId = localStorage.getItem(STORAGE_KEYS.userId);
  const username = localStorage.getItem(STORAGE_KEYS.username);

  if (!token || !role || !userId || !username || token === "undefined" || token === "null") {
    return null;
  }

  return {
    token,
    role,
    userId: Number(userId),
    username,
  };
}

export function setSession(session: AuthSession) {
  localStorage.setItem(STORAGE_KEYS.token, session.token);
  localStorage.setItem(STORAGE_KEYS.role, session.role);
  localStorage.setItem(STORAGE_KEYS.userId, String(session.userId));
  localStorage.setItem(STORAGE_KEYS.username, session.username);
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEYS.token);
  localStorage.removeItem(STORAGE_KEYS.role);
  localStorage.removeItem(STORAGE_KEYS.userId);
  localStorage.removeItem(STORAGE_KEYS.username);
}

export function getDefaultRoute(role?: string | null) {
  return role === "ROLE_ADMIN" ? "/admin/dashboard" : "/user/dashboard";
}
