import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from "react-router-dom";
import { useEffect, useState } from "react";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AdminDashboard from "./pages/AdminDashboard";
import AddStockPage from "./pages/AddStockPage";
import EditStockPage from "./pages/EditStockPage";
import ManageUsersPage from "./pages/ManageUsersPage";
import UserDashboard from "./pages/UserDashboard";
import PortfolioPage from "./pages/PortfolioPage";
import StocksPage from "./pages/StocksPage";
import TradesPage from "./pages/TradesPage";
import Layout from "./components/Layout";

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirect root to login */}
        <Route path="/" element={<Navigate to="/login" />} />
        
        {/* Public pages */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Protected routes with Layout */}
        <Route element={<AuthLayout />}>
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/admin/add" element={<AddStockPage />} />
          <Route path="/admin/edit/:id" element={<EditStockPage />} />
          <Route path="/admin/users" element={<ManageUsersPage />} />
          <Route path="/user/dashboard" element={<UserDashboard />} />
          <Route path="/user/portfolio" element={<PortfolioPage />} />
          <Route path="/user/stocks" element={<StocksPage />} />
          <Route path="/user/trades" element={<TradesPage />} />
        </Route>
        
        {/* Catch all - redirect to login */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

function AuthLayout() {
  const [isValid, setIsValid] = useState<boolean | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token && token !== "undefined" && token !== "null" && token.length > 0) {
      setIsValid(true);
    } else {
      setIsValid(false);
    }
  }, []);

  if (isValid === null) {
    return <div>Loading...</div>;
  }

  if (!isValid) {
    return <Navigate to="/login" replace />;
  }

  return (
    <Layout>
      <Outlet />
    </Layout>
  );
}

export default App;
