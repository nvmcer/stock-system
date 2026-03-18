import React, { type ReactNode } from "react";
import { useNavigate, Outlet } from "react-router-dom";
import "./Layout.css";

interface LayoutProps {
  children?: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("role");
  const user = localStorage.getItem("username");
  const isAuthenticated = token && token !== "undefined" && token !== "null" && token.length > 0;

  if (!isAuthenticated) {
    localStorage.clear();
    navigate("/login");
    return null;
  }

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <div className="app-shell">
      <header className="app-header">
        <div
          className="brand"
          onClick={() =>
            navigate(userRole === "ROLE_ADMIN" ? "/admin/dashboard" : "/user/dashboard")
          }
        >
          StocksBoard
        </div>
        
        <nav className="nav">
          <div className="nav-spacer" />
          
          <div className="user">
            <span style={{ fontSize: '0.85rem', color: 'var(--muted)', marginRight: '6px' }}>👤</span>
            <strong style={{ color: '#e6eef8' }}>{user || "Guest"}</strong>
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
