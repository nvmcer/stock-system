import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

interface Stock {
  id: number;
  symbol: string;
  name: string;
  price: number;
  updatedAt: string;
}

interface PriceUpdateResult {
  totalStocks: number;
  updatedCount: number;
  failedCount: number;
  failedSymbols: string[];
}

function AdminDashboard() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [isUpdating, setIsUpdating] = useState(false);
  const navigate = useNavigate();

  const fetchStocks = async () => {
    const res = await api.get("/api/stocks");
    if (res.data.success) {
      setStocks(res.data.data || []);
      return;
    }

    throw new Error(res.data.message || "Failed to fetch stocks");
  };

  useEffect(() => {
    void (async () => {
      try {
        await fetchStocks();
      } catch (err) {
        console.error("Failed to fetch stocks:", err);
      }
    })();
  }, []);

  const handleDelete = async (id: number) => {
    try {
      const res = await api.delete(`/api/stocks/${id}`);
      if (res.data.success) {
        setStocks(stocks.filter(stock => stock.id !== id));
      } else {
        alert("Failed to delete: " + res.data.message);
      }
    } catch (err) {
      console.error("Delete failed:", err);
      const error = err as { response?: { data?: { message?: string } }, message?: string };
      alert("Failed to delete: " + (error.response?.data?.message || error.message));
    }
  };

  const buildUpdateMessage = (message: string, result?: PriceUpdateResult) => {
    if (!result) {
      return message;
    }

    if (result.totalStocks === 0) {
      return message;
    }

    const summary = `Updated ${result.updatedCount}/${result.totalStocks} stock(s)`;
    if (result.failedCount === 0) {
      return `${message} ${summary}.`;
    }

    const failedSymbols = result.failedSymbols.length > 0
      ? ` Failed: ${result.failedSymbols.join(", ")}.`
      : "";
    return `${message} ${summary}. ${result.failedCount} failed.${failedSymbols}`;
  };

  async function updatePrices() {
    setIsUpdating(true);

    try {
      const res = await api.post("/api/stocks/update-prices", {});
      const result = res.data.data as PriceUpdateResult | undefined;

      await fetchStocks();
      alert(buildUpdateMessage(res.data.message || "Prices updated.", result));
    } catch (err) {
      console.error("Price update failed:", err);
      const error = err as {
        response?: { data?: { message?: string; data?: PriceUpdateResult } };
        message?: string;
      };
      const message = error.response?.data?.message || error.message || "Failed to update prices";
      const result = error.response?.data?.data;
      alert(buildUpdateMessage(message, result));
    } finally {
      setIsUpdating(false);
    }
  }

  const lastUpdatedTime = stocks.length > 0
    ? new Date(Math.max(...stocks.map(stock => new Date(stock.updatedAt).getTime()))).toLocaleString()
    : "Never";

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
        <div>
          <h1>Admin Dashboard</h1>
          <small style={{ color: "#aaa" }}>Last updated: {lastUpdatedTime}</small>
        </div>
        <div style={{ display: "flex", gap: "8px" }}>
          <button className="primary" onClick={() => navigate("/admin/users")}>Manage Users</button>
          <button className="primary" onClick={updatePrices} disabled={isUpdating}>
            {isUpdating ? "Updating..." : "Update Prices"}
          </button>
          <button className="primary" onClick={() => navigate("/admin/add")}>+ Add Stock</button>
        </div>
      </div>

      <div className="card" style={{ marginTop: "20px" }}>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
              <th style={{ padding: "12px", textAlign: "left" }}>Symbol</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Name</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Price</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map(stock => (
              <tr key={stock.id} style={{ borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
                <td style={{ padding: "12px" }}><strong>{stock.symbol}</strong></td>
                <td style={{ padding: "12px" }}>{stock.name}</td>
                <td style={{ padding: "12px" }}>${stock.price}</td>
                <td style={{ padding: "12px", textAlign: "right" }}>
                  <button onClick={() => navigate(`/admin/edit/${stock.id}`)} style={{ marginRight: "8px" }}>Edit</button>
                  <button onClick={() => handleDelete(stock.id)} style={{ background: "rgba(239,68,68,0.2)", color: "#ef4444" }}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default AdminDashboard;
