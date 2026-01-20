import React from "react";
import { useNavigate } from "react-router-dom";
import "./Layout.css";

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Get user info from localStorage
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const username = localStorage.getItem("username");

  return (
    <div className="app-shell">
      <header className="app-header">
        {/* Header brand/logo - clicking navigates to appropriate dashboard */}
        <div
          className="brand"
          onClick={() =>
            navigate(role === "ROLE_ADMIN" ? "/admin/dashboard" : "/user/dashboard")
          }
        >
          StocksBoard
        </div>
        
        {/* Top navigation bar */}
        <nav className="nav">
          <div className="nav-spacer" />
          
          {/* Display current user */}
          <div className="user">
            <span style={{ fontSize: '0.85rem', color: 'var(--muted)', marginRight: '6px' }}>👤</span>
            <strong style={{ color: '#e6eef8' }}>{username || "Guest"}</strong>
          </div>
          
          {/* Logout button */}
          <button
            className="btn-ghost"
            onClick={() => {
              // Clear user session from localStorage
              localStorage.removeItem("token");
              localStorage.removeItem("role");
              localStorage.removeItem("userId");
              localStorage.removeItem("username");
              navigate("/login");
            }}
          >
            Logout
          </button>
        </nav>
      </header>

      {/* Main content area */}
      <main className="container">{children}</main>

      {/* Footer */}
      <footer className="app-footer">© {new Date().getFullYear()} StocksBoard</footer>
    </div>
  );
};

export default Layout;
