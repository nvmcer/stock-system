import React from "react";
import { useNavigate } from "react-router-dom";
import "./Layout.css";

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const username = localStorage.getItem("username");

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
          <button
            className="btn-ghost"
            onClick={() => {
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

      <main className="container">{children}</main>

      <footer className="app-footer">© {new Date().getFullYear()} StocksBoard</footer>
    </div>
  );
};

export default Layout;
