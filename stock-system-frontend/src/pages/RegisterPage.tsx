import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

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
      const res = await api.post("/api/auth/login", { username, password });
      
      // Store authentication info
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("role", res.data.role);
      localStorage.setItem("userId", String(res.data.userId));
      localStorage.setItem("username", res.data.username);
      
      // Redirect to appropriate dashboard
      if (res.data.role === "ROLE_ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/user/dashboard");
      }
    } catch (err: any) {
      alert("Registration failed: " + (err.response?.data?.message || err.message));
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
