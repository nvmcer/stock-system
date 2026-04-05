import { BrowserRouter as Router, Navigate, Outlet, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import AddStockPage from "./pages/AddStockPage";
import AdminDashboard from "./pages/AdminDashboard";
import EditStockPage from "./pages/EditStockPage";
import LoginPage from "./pages/LoginPage";
import ManageUsersPage from "./pages/ManageUsersPage";
import PortfolioPage from "./pages/PortfolioPage";
import RegisterPage from "./pages/RegisterPage";
import StocksPage from "./pages/StocksPage";
import TradesPage from "./pages/TradesPage";
import UserDashboard from "./pages/UserDashboard";
import { getDefaultRoute, getSession } from "./services/auth";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route element={<ProtectedRoute allowedRoles={["ROLE_ADMIN"]} />}>
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/admin/add" element={<AddStockPage />} />
          <Route path="/admin/edit/:id" element={<EditStockPage />} />
          <Route path="/admin/users" element={<ManageUsersPage />} />
        </Route>

        <Route element={<ProtectedRoute allowedRoles={["ROLE_USER", "ROLE_ADMIN"]} />}>
          <Route path="/user/dashboard" element={<UserDashboard />} />
          <Route path="/user/portfolio" element={<PortfolioPage />} />
          <Route path="/user/stocks" element={<StocksPage />} />
          <Route path="/user/trades" element={<TradesPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

function ProtectedRoute({ allowedRoles }: { allowedRoles: string[] }) {
  const session = getSession();

  if (!session) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(session.role)) {
    return <Navigate to={getDefaultRoute(session.role)} replace />;
  }

  return (
    <Layout>
      <Outlet />
    </Layout>
  );
}

export default App;
