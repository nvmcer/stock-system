import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

interface Stock {
  id: number;
  symbol: string;
  name: string;
  price: number;
}

function AdminDashboard() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const navigate = useNavigate();

  // 銘柄リスト
  useEffect(() => {
    const token = localStorage.getItem("token");
    api.get<Stock[]>("/api/stocks", {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => setStocks(res.data))
    .catch(err => console.error("Failed to fetch stocks:", err));
  }, []);

  // 名柄削除
  const handleDelete = async (id: number) => {
    const token = localStorage.getItem("token");
    try {
      await api.delete(`/api/stocks/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setStocks(stocks.filter(stock => stock.id !== id));
    } catch (err) {
      console.error("Delete failed:", err);
    }
  };

  // 最新の価格に更新
  async function updatePrices() {
    const token = localStorage.getItem("token");

    const res = await api.post(
      "/api/stocks/update-prices",
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    alert(res.data.message);
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Admin Dashboard</h1>
        <button className="primary" onClick={updatePrices}>Update Prices</button>
        <button className="primary" onClick={() => navigate("/admin/add")}>+ Add Stock</button>
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