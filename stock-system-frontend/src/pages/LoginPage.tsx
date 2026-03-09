import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

// API response format from login endpoint (wrapped in ApiResponse)
interface LoginResponse {
  success: boolean;
  code: string;
  message: string;
  data: {
    token: string;        // JWT token for authentication
    role: string;         // User role (ROLE_ADMIN or ROLE_USER)
    userId: number;      // User ID
    username: string;    // Username
  };
  timestamp: string;
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
      
      // Debug: log the full response
      console.log("Login response:", res.data);
      
      // Access data from ApiResponse envelope (res.data is the envelope, res.data.data contains the payload)
      const loginData = res.data.data;
      
      // Validate loginData exists
      if (!loginData || !loginData.token) {
        console.error("Invalid login response:", loginData);
        alert("Login failed: Invalid response from server");
        return;
      }
      
      // Store authentication info in localStorage for future requests
      localStorage.setItem("token", loginData.token);
      localStorage.setItem("role", loginData.role);
      localStorage.setItem("userId", String(loginData.userId));
      localStorage.setItem("username", loginData.username);
      
      // Redirect to appropriate dashboard based on user role
      if (loginData.role === "ROLE_ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/user/dashboard");
      }
    } catch (err: any) {
      console.error("Login error:", err);
      // Handle both wrapped and unwrapped error responses
      const errorMessage = err.response?.data?.data?.message || err.response?.data?.message || err.message;
      alert("Login failed: " + errorMessage);
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