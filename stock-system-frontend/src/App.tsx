import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import AdminDashboard from "./pages/AdminDashboard";
import AddStockPage from "./pages/AddStockPage";
import EditStockPage from "./pages/EditStockPage";
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