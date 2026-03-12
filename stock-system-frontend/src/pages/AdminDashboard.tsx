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

function AdminDashboard() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const navigate = useNavigate();

  // List of stocks
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token || token === "undefined") {
      navigate("/login");
      return;
    }
    
    api.get("/api/stocks", {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => {
      // Handle ApiResponse envelope
      if (res.data.success) {
        setStocks(res.data.data || []);
      } else {
        console.error("Failed to fetch stocks:", res.data.message);
      }
    })
    .catch(err => console.error("Failed to fetch stocks:", err));
  }, []);

  // Delete stock
  const handleDelete = async (id: number) => {
    const token = localStorage.getItem("token");
    try {
      const res = await api.delete(`/api/stocks/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (res.data.success) {
        setStocks(stocks.filter(stock => stock.id !== id));
      } else {
        alert("Failed to delete: " + res.data.message);
      }
    } catch (err: any) {
      console.error("Delete failed:", err);
      alert("Failed to delete: " + (err.response?.data?.message || err.message));
    }
  };

  // Update prices to latest
  async function updatePrices() {
    const token = localStorage.getItem("token");

    try {
      // Call backend to update prices from market data service
      const res = await api.post(
        "/api/stocks/update-prices",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (res.data.success) {
        alert(res.data.message || "Prices updated successfully");
        
        // Fetch updated stock prices from server
        const updatedStocks = await api.get("/api/stocks", {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        // Handle ApiResponse envelope
        if (updatedStocks.data.success) {
          setStocks(updatedStocks.data.data || []);
        }
        alert("Stock prices refreshed!");
      } else {
        alert("Failed to update prices: " + res.data.message);
      }
    } catch (err: any) {
      console.error("Price update failed:", err);
      alert("Failed to update prices: " + (err.response?.data?.message || err.message));
    }
  }

  // Compute the latest update timestamp from stocks
  const lastUpdatedTime = stocks.length > 0 
    ? new Date(Math.max(...stocks.map(s => new Date(s.updatedAt).getTime()))).toLocaleString()
    : 'Never';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <div>
          <h1>Admin Dashboard</h1>
          <small style={{ color: '#aaa' }}>Last updated: {lastUpdatedTime}</small>
        </div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button className="primary" onClick={() => navigate("/admin/users")}>👥 Manage Users</button>
          <button className="primary" onClick={updatePrices}>Update Prices</button>
          <button className="primary" onClick={() => navigate("/admin/add")}>+ Add Stock</button>
        </div>
      </div>

      <div className="card" style={{ marginTop: '20px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
              <th style={{ padding: '12px', textAlign: 'left' }}>Symbol</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Name</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Price</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map(stock => (
              <tr key={stock.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                <td style={{ padding: '12px' }}><strong>{stock.symbol}</strong></td>
                <td style={{ padding: '12px' }}>{stock.name}</td>
                <td style={{ padding: '12px' }}>${stock.price}</td>
                <td style={{ padding: '12px', textAlign: 'right' }}>
                  <button onClick={() => navigate(`/admin/edit/${stock.id}`)} style={{ marginRight: '8px' }}>Edit</button>
                  <button onClick={() => handleDelete(stock.id)} style={{ background: 'rgba(239,68,68,0.2)', color: '#ef4444' }}>Delete</button>
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