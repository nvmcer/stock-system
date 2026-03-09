import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

// API response format from login endpoint (wrapped in ApiResponse)
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

function RegisterPage() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const navigate = useNavigate();

  // Handle registration form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      alert("Username and password are required");
      return;
    }

    try {
      // Create new user account
      await api.post("/api/auth/register", { username, password });
      
      // Auto-login after successful registration
      const res = await api.post<LoginResponse>("/api/auth/login", { username, password });
      
      // Access data from ApiResponse envelope
      const loginData = res.data.data;
      
      // Store authentication info
      localStorage.setItem("token", loginData.token);
      localStorage.setItem("role", loginData.role);
      localStorage.setItem("userId", String(loginData.userId));
      localStorage.setItem("username", loginData.username);
      
      // Redirect to appropriate dashboard
      if (loginData.role === "ROLE_ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/user/dashboard");
      }
    } catch (err: any) {
      // Handle both wrapped and unwrapped error responses
      const errorMessage = err.response?.data?.data?.message || err.response?.data?.message || err.message;
      alert("Registration failed: " + errorMessage);
    }
  };

  return (
    <div className="login-page">
      <div className="card login-card">
        <h1>Register</h1>
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

          <button type="submit" className="primary" style={{ alignSelf: 'center', marginTop: '8px' }}>Register</button>
        </form>
      </div>
    </div>
  );
}

export default RegisterPage;
