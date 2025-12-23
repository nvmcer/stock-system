import { useEffect, useState } from "react";
import axios from "axios";

function PortfolioPage() {
  const [portfolio, setPortfolio] = useState<any[]>([]);
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");
  const totalProfit = portfolio.reduce(
    (sum, p) => sum + Number(p.totalProfit),
    0
  );

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const res = await axios.get(`/api/portfolio?userId=${userId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setPortfolio(res.data);
      } catch (err: any) {
        alert("Failed to get Stock List: " + (err.response?.data?.message || err.message));
      }
    };
    if (userId) fetchPortfolio();
  }, [userId, token]);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0 }}>My Stock Portfolio</h2>
        <div style={{ padding: '12px 16px', background: totalProfit >= 0 ? 'rgba(34,197,94,0.1)' : 'rgba(239,68,68,0.1)', borderRadius: '8px', color: totalProfit >= 0 ? '#22c55e' : '#ef4444' }}>
          <strong>Total P/L: {totalProfit.toFixed(2)}</strong>
        </div>
      </div>
      <div className="card">
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
              <th style={{ padding: '12px', textAlign: 'left' }}>Code</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Name</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Qty</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Avg Cost</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Price</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Realized P/L</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Unrealized P/L</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Total P/L</th>
            </tr>
          </thead>
          <tbody>
            {portfolio.map((p: any) => (
              <tr key={p.symbol} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                <td style={{ padding: '12px' }}><strong>{p.symbol}</strong></td>
                <td style={{ padding: '12px' }}>{p.name}</td>
                <td style={{ padding: '12px', textAlign: 'right' }}>{p.quantity}</td>
                <td style={{ padding: '12px', textAlign: 'right' }}>${p.avgCost?.toFixed(2)}</td>
                <td style={{ padding: '12px', textAlign: 'right' }}>${p.currentPrice?.toFixed(2)}</td>
                <td style={{ padding: '12px', textAlign: 'right', color: p.realizedProfit >= 0 ? '#22c55e' : '#ef4444' }}>{p.realizedProfit?.toFixed(2)}</td>
                <td style={{ padding: '12px', textAlign: 'right', color: p.unrealizedProfit >= 0 ? '#22c55e' : '#ef4444' }}>{p.unrealizedProfit?.toFixed(2)}</td>
                <td style={{ padding: '12px', textAlign: 'right', color: p.totalProfit >= 0 ? '#22c55e' : '#ef4444' }}><strong>{p.totalProfit?.toFixed(2)}</strong></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default PortfolioPage;