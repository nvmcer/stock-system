import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function StocksPage() {
  const [stocks, setStocks] = useState<any[]>([]);
  const [quantities, setQuantities] = useState<{ [key: string]: number }>({});
  const [prices, setPrices] = useState<{ [key: string]: number }>({});
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    if (!token || token === "undefined") {
      navigate("/login");
      return;
    }

    const fetchStocks = async () => {
      try {
        const res = await api.get("/api/stocks", {
          headers: { Authorization: `Bearer ${token}` }
        });
        // Handle ApiResponse envelope
        if (res.data.success) {
          setStocks(res.data.data || []);
        } else {
          alert("Failed to get Stock List: " + res.data.message);
        }
      } catch (err: any) {
        alert("Failed to get Stock List: " + (err.response?.data?.message || err.message));
      }
    };
    fetchStocks();
  }, [token, navigate]);

  const handleBuy = async (symbol: string) => {
    try {
      const res = await api.post(`/api/trades/buy?userId=${userId}`,
        { symbol, quantity: quantities[symbol] || 0, price: prices[symbol] || 0 },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (res.data.success) {
        alert("Successfully bought");
      } else {
        alert("Failed to buy: " + res.data.message);
      }
    } catch (err: any) {
      alert("Failed to buy: " + (err.response?.data?.message || err.message));
    }
  };

  const handleSell = async (symbol: string) => {
    try {
      const res = await api.post(`/api/trades/sell?userId=${userId}`,
        { symbol, quantity: quantities[symbol] || 0, price: prices[symbol] || 0 },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (res.data.success) {
        alert("Successfully sold");
      } else {
        alert("Failed to sell: " + res.data.message);
      }
    } catch (err: any) {
      alert("Failed to sell: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div>
      <h2>Stock List</h2>
      <div className="card">
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
              <th style={{ padding: '12px', textAlign: 'left' }}>Code</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Name</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Price</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Quantity</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Trade Price</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map((s: any) => (
              <tr key={s.symbol} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)', height: '50px', verticalAlign: 'middle' }}>
                <td style={{ padding: '12px', verticalAlign: 'middle' }}><strong>{s.symbol}</strong></td>
                <td style={{ padding: '12px', verticalAlign: 'middle' }}>{s.name}</td>
                <td style={{ padding: '12px', verticalAlign: 'middle' }}>${s.price?.toFixed(2)}</td>
                <td style={{ padding: '12px', verticalAlign: 'middle' }}>
                  <input
                    type="number"
                    value={quantities[s.symbol] || ""}
                    onChange={e =>
                      setQuantities({ ...quantities, [s.symbol]: Number(e.target.value) })
                    }
                    placeholder="Qty"
                    style={{ width: '70px', padding: '6px 8px' }}
                  />
                </td>
                <td style={{ padding: '12px', verticalAlign: 'middle' }}>
                  <input
                    type="number"
                    value={prices[s.symbol] || ""}
                    onChange={e =>
                      setPrices({ ...prices, [s.symbol]: Number(e.target.value) })
                    }
                    placeholder="Price"
                    style={{ width: '70px', padding: '6px 8px' }}
                  />
                </td>
                <td style={{ padding: '12px', verticalAlign: 'middle' }}>
                  <button onClick={() => handleBuy(s.symbol)} className="primary" style={{ marginRight: '8px', padding: '6px 14px', fontSize: '0.9rem', minWidth: '60px' }}>Buy</button>
                  <button onClick={() => handleSell(s.symbol)} style={{ background: 'rgba(239,68,68,0.2)', color: '#ef4444', padding: '6px 14px', fontSize: '0.9rem', minWidth: '60px' }}>Sell</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default StocksPage;
