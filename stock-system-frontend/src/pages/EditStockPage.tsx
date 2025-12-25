import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

function EditStockPage() {
  const { id } = useParams<{ id: string }>();
  const [symbol, setSymbol] = useState("");
  const [name, setName] = useState("");
  const [price, setPrice] = useState<number>(0);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    api.get(`/api/stocks/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => {
      setSymbol(res.data.symbol)
      setName(res.data.name);
      setPrice(res.data.price);
    })
    .catch(err => console.error("Failed to load stock:", err));
  }, [id]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    await api.put(`/api/stocks/${id}`, { symbol, name, price }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    navigate("/admin/dashboard");
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <h1>Edit Stock</h1>
      <div className="card">
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <label style={{ fontSize: '0.9rem', color: 'var(--muted)', marginBottom: '6px', fontWeight: '500' }}>
              Stock Symbol
            </label>
            <input 
              value={symbol} 
              onChange={e => setSymbol(e.target.value)} 
              placeholder="e.g. TSLA" 
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
          <button type="submit" className="primary" style={{ marginTop: '8px', padding: '10px 20px', alignSelf: 'flex-start' }}>Update Stock</button>
        </form>
      </div>
    </div>
  );
}

export default EditStockPage;