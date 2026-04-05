import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { getDefaultRoute, setSession } from "../services/auth";

interface LoginResponse {
  success: boolean;
  code: string;
  message: string;
  data: {
    token: string;
    role: string;
    userId: number;
    username: string;
  };
  timestamp: string;
}

function LoginPage() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      alert("Username and password are required");
      return;
    }

    try {
      const res = await api.post<LoginResponse>("/api/auth/login", { username, password });
      const loginData = res.data.data;

      if (!loginData || !loginData.token) {
        alert("Login failed: Invalid response from server");
        return;
      }

      setSession(loginData);
      navigate(getDefaultRoute(loginData.role));
    } catch (err) {
      const error = err as { response?: { data?: { data?: { message?: string }, message?: string } }, message?: string };
      const errorMessage = error.response?.data?.data?.message || error.response?.data?.message || error.message;
      alert("Login failed: " + errorMessage);
    }
  };

  return (
    <div className="login-page">
      <div className="card login-card">
        <h1>Login</h1>
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ boxSizing: "border-box" }}
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ boxSizing: "border-box" }}
          />

          <button type="submit" className="primary" style={{ alignSelf: "center", marginTop: "8px" }}>Login</button>
          <button
            type="button"
            className="secondary"
            style={{ alignSelf: "center", marginTop: "8px" }}
            onClick={() => navigate("/register")}
          >
            Register
          </button>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
