import React, { useEffect, useState, type ReactNode } from "react";
import { useNavigate, Outlet } from "react-router-dom";
import "./Layout.css";

interface LayoutProps {
  children?: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(true);
  const [role, setRole] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userRole = localStorage.getItem("role");
    const user = localStorage.getItem("username");

    // Check if token exists and is valid (not "undefined" or null)
    if (!token || token === "undefined" || token === "null") {
      // Token is missing or invalid - redirect to login
      localStorage.clear();
      navigate("/login");
      setIsAuthenticated(false);
      return;
    }

    setRole(userRole);
    setUsername(user);
    setIsAuthenticated(true);
  }, [navigate]);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  if (!isAuthenticated) {
    return null; // or a loading spinner
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <div
          className="brand"
          onClick={() =>
            navigate(role === "ROLE_ADMIN" ? "/admin/dashboard" : "/user/dashboard")
          }
        >
          StocksBoard
        </div>
        
        <nav className="nav">
          <div className="nav-spacer" />
          
          <div className="user">
            <span style={{ fontSize: '0.85rem', color: 'var(--muted)', marginRight: '6px' }}>👤</span>
            <strong style={{ color: '#e6eef8' }}>{username || "Guest"}</strong>
          </div>
          
          <button className="btn-ghost" onClick={handleLogout}>
            Logout
          </button>
        </nav>
      </header>

      <main className="container">
        {children || <Outlet />}
      </main>

      <footer className="app-footer">© {new Date().getFullYear()} StocksBoard</footer>
    </div>
  );
};

export default Layout;
