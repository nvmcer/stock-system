import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function AddStockPage() {
  const [symbol, setSymbol] = useState("");
  const [name, setName] = useState("");
  const [price, setPrice] = useState<number>(0);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    await axios.post("/api/stocks", { symbol, name, price }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    navigate("/admin/dashboard");
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