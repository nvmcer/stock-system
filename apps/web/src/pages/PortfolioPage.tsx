import { useEffect, useState } from "react";
import api from "../services/api";
import type { PortfolioItem } from "../services/types";

function PortfolioPage() {
  const [portfolio, setPortfolio] = useState<PortfolioItem[]>([]);
  const totalProfit = portfolio.reduce((sum, item) => sum + Number(item.totalProfit), 0);

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const res = await api.get("/api/portfolio");
        if (res.data.success) {
          setPortfolio(res.data.data || []);
        } else {
          alert("Failed to get portfolio: " + res.data.message);
        }
      } catch (err) {
        const error = err as { response?: { data?: { message?: string } }, message?: string };
        alert("Failed to get portfolio: " + (error.response?.data?.message || error.message));
      }
    };

    void fetchPortfolio();
  }, []);

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
        <h2 style={{ margin: 0 }}>My Stock Portfolio</h2>
        <div style={{ padding: "12px 16px", background: totalProfit >= 0 ? "rgba(34,197,94,0.1)" : "rgba(239,68,68,0.1)", borderRadius: "8px", color: totalProfit >= 0 ? "#22c55e" : "#ef4444" }}>
          <strong>Total P/L: {totalProfit.toFixed(2)}</strong>
        </div>
      </div>
      <div className="card">
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
              <th style={{ padding: "12px", textAlign: "left" }}>Code</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Name</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Qty</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Avg Cost</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Price</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Realized P/L</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Unrealized P/L</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Total P/L</th>
            </tr>
          </thead>
          <tbody>
            {portfolio.map((item) => (
              <tr key={item.symbol} style={{ borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
                <td style={{ padding: "12px" }}><strong>{item.symbol}</strong></td>
                <td style={{ padding: "12px" }}>{item.name}</td>
                <td style={{ padding: "12px", textAlign: "right" }}>{item.quantity}</td>
                <td style={{ padding: "12px", textAlign: "right" }}>${item.avgCost?.toFixed(2)}</td>
                <td style={{ padding: "12px", textAlign: "right" }}>${item.currentPrice?.toFixed(2)}</td>
                <td style={{ padding: "12px", textAlign: "right", color: item.realizedProfit >= 0 ? "#22c55e" : "#ef4444" }}>{item.realizedProfit?.toFixed(2)}</td>
                <td style={{ padding: "12px", textAlign: "right", color: item.unrealizedProfit >= 0 ? "#22c55e" : "#ef4444" }}>{item.unrealizedProfit?.toFixed(2)}</td>
                <td style={{ padding: "12px", textAlign: "right", color: item.totalProfit >= 0 ? "#22c55e" : "#ef4444" }}><strong>{item.totalProfit?.toFixed(2)}</strong></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default PortfolioPage;
