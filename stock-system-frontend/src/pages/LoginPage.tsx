import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

// API response format from login endpoint
interface LoginResponse {
  token: string;        // JWT token for authentication
  role: string;         // User role (ROLE_ADMIN or ROLE_USER)
  userId: string;       // User ID
  username: string;     // Username
}

function LoginPage() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const navigate = useNavigate();

  // Handle login form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      alert("Username and password are required");
      return;
    }

    try {
      // Send login request to backend
      const res = await api.post<LoginResponse>("/api/auth/login", { username, password });
      
      // Store authentication info in localStorage for future requests
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("role", res.data.role);
      localStorage.setItem("userId", String(res.data.userId));
      localStorage.setItem("username", res.data.username);
      
      // Redirect to appropriate dashboard based on user role
      if (res.data.role === "ROLE_ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/user/dashboard");
      }
    } catch (err: any) {
      alert("Login failed: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div className="login-page">
      <div className="card login-card">
        <h1>Login</h1>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ boxSizing: 'border-box' }}
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ boxSizing: 'border-box' }}
          />

          <button type="submit" className="primary" style={{ alignSelf: 'center', marginTop: '8px' }}>Login</button>
          <button
            type="button"
            className="secondary"
            style={{ alignSelf: 'center', marginTop: '8px' }}
            onClick={() => navigate('/register')}
          >
            Register
          </button>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;