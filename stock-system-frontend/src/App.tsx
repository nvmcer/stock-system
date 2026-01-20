import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
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
        
        {/* Admin pages - requires Layout wrapper for navigation */}
        <Route
          path="/admin/dashboard"
          element={
            <Layout>
              <AdminDashboard />
            </Layout>
          }
        />
        <Route
          path="/admin/add"
          element={
            <Layout>
              <AddStockPage />
            </Layout>
          }
        />
        <Route
          path="/admin/edit/:id"
          element={
            <Layout>
              <EditStockPage />
            </Layout>
          }
        />
        <Route
          path="/admin/users"
          element={
            <Layout>
              <ManageUsersPage />
            </Layout>
          }
        />
        
        {/* User pages - requires Layout wrapper for navigation */}
        <Route
          path="/user/dashboard"
          element={
            <Layout>
              <UserDashboard />
            </Layout>
          }
        />
        <Route
          path="/user/portfolio"
          element={
            <Layout>
              <PortfolioPage />
            </Layout>
          }
        />
        <Route
          path="/user/stocks"
          element={
            <Layout>
              <StocksPage />
            </Layout>
          }
        />
        <Route
          path="/user/trades"
          element={
            <Layout>
              <TradesPage />
            </Layout>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;