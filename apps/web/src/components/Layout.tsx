import React, { type ReactNode } from "react";
import { useNavigate, Outlet } from "react-router-dom";
import { clearSession, getDefaultRoute, getSession } from "../services/auth";
import "./Layout.css";

interface LayoutProps {
  children?: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const session = getSession();

  const handleLogout = () => {
    clearSession();
    navigate("/login");
  };

  return (
    <div className="app-shell">
      <header className="app-header">
        <div
          className="brand"
          onClick={() => navigate(getDefaultRoute(session?.role))}
        >
          StocksBoard
        </div>

        <nav className="nav">
          <div className="nav-spacer" />

          <div className="user">
            <span style={{ fontSize: "0.85rem", color: "var(--muted)", marginRight: "6px" }}>👤</span>
            <strong style={{ color: "#e6eef8" }}>{session?.username || "Guest"}</strong>
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
