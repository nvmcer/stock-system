import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from "react-router-dom";
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
        <Route path="/" element={<Navigate to="/login" />} />
        
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
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
        
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

function AuthLayout() {
  const token = localStorage.getItem("token");
  const isValid = token && token !== "undefined" && token !== "null" && token.length > 0;

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