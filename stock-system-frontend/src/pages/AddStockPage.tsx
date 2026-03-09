import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function AddStockPage() {
  const [symbol, setSymbol] = useState("");
  const [name, setName] = useState("");
  const [price, setPrice] = useState<number>(0);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token || token === "undefined") {
      navigate("/login");
    }
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    try {
      const res = await api.post("/api/stocks", { symbol, name, price }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (res.data.success) {
        navigate("/admin/dashboard");
      } else {
        alert("Failed to add stock: " + res.data.message);
      }
    } catch (err: any) {
      alert("Failed to add stock: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <h1>Add Stock</h1>
      <div className="card">
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ fontSize: '0.9rem', color: 'var(--muted)', marginBottom: '6px', fontWeight: '500' }}>
              Stock Symbol
            </label>
            <input 
              value={symbol} 
              onChange={e => setSymbol(e.target.value)} 
              placeholder="e.g. TSLA, AAPL" 
              required
              style={{ margin: '0' }}
            />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ fontSize: '0.9rem', color: 'var(--muted)', marginBottom: '6px', fontWeight: '500' }}>
              Company Name
            </label>
            <input 
              value={name} 
              onChange={e => setName(e.target.value)} 
              placeholder="e.g. Tesla Inc." 
              required
              style={{ margin: '0' }}
            />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ fontSize: '0.9rem', color: 'var(--muted)', marginBottom: '6px', fontWeight: '500' }}>
              Price ($)
            </label>
            <input 
              type="number" 
              value={price} 
              onChange={e => setPrice(Number(e.target.value))} 
              placeholder="0.00" 
              required
              style={{ margin: '0' }}
            />
          </div>
          <button type="submit" className="primary" style={{ marginTop: '8px', padding: '10px 20px', alignSelf: 'flex-start' }}>Save Stock</button>
        </form>
      </div>
    </div>
  );
}

export default AddStockPage;