import { useEffect, useState } from "react";
import api from "../services/api";

function TradesPage() {
  const [trades, setTrades] = useState<any[]>([]);
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchTrades = async () => {
      try {
        const res = await api.get(`/api/trades/history?userId=${userId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setTrades(res.data);
      } catch (err: any) {
        alert("Failed to get Trade History: " + (err.response?.data?.message || err.message));
      }
    };
    if (userId) fetchTrades();
  }, [userId, token]);

  return (
    <div>
      <h2>Trade History</h2>
      <div className="card">
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
              <th style={{ padding: '12px', textAlign: 'left' }}>Time</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Stock</th>
              <th style={{ padding: '12px', textAlign: 'center' }}>Type</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Quantity</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Price</th>
            </tr>
          </thead>
          <tbody>
            {trades.map((t: any) => (
              <tr key={t.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                <td style={{ padding: '12px' }}>{new Date(t.timestamp).toLocaleString()}</td>
                <td style={{ padding: '12px' }}><strong>{t.stockSymbol}</strong></td>
                <td style={{ padding: '12px', textAlign: 'center', color: t.type === 'BUY' ? '#22c55e' : '#ef4444' }}>
                  <strong>{t.type}</strong>
                </td>
                <td style={{ padding: '12px', textAlign: 'right' }}>{t.quantity}</td>
                <td style={{ padding: '12px', textAlign: 'right' }}>${t.price?.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default TradesPage;