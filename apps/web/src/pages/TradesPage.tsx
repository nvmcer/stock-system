import { useEffect, useState } from "react";
import api from "../services/api";
import type { Trade } from "../services/types";

function TradesPage() {
  const [trades, setTrades] = useState<Trade[]>([]);

  useEffect(() => {
    const fetchTrades = async () => {
      try {
        const res = await api.get("/api/trades/history");
        if (res.data.success) {
          setTrades(res.data.data || []);
        } else {
          alert("Failed to get Trade History: " + res.data.message);
        }
      } catch (err) {
        const error = err as { response?: { data?: { message?: string } }, message?: string };
        alert("Failed to get Trade History: " + (error.response?.data?.message || error.message));
      }
    };

    void fetchTrades();
  }, []);

  return (
    <div>
      <h2>Trade History</h2>
      <div className="card">
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
              <th style={{ padding: "12px", textAlign: "left" }}>Time</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Stock</th>
              <th style={{ padding: "12px", textAlign: "center" }}>Type</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Quantity</th>
              <th style={{ padding: "12px", textAlign: "right" }}>Price</th>
            </tr>
          </thead>
          <tbody>
            {trades.map((trade) => (
              <tr key={trade.id} style={{ borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
                <td style={{ padding: "12px" }}>{new Date(trade.timestamp).toLocaleString()}</td>
                <td style={{ padding: "12px" }}><strong>{trade.stockSymbol}</strong></td>
                <td style={{ padding: "12px", textAlign: "center", color: trade.type === "BUY" ? "#22c55e" : "#ef4444" }}>
                  <strong>{trade.type}</strong>
                </td>
                <td style={{ padding: "12px", textAlign: "right" }}>{trade.quantity}</td>
                <td style={{ padding: "12px", textAlign: "right" }}>${trade.price?.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default TradesPage;
