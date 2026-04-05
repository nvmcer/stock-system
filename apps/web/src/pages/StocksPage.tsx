import { useEffect, useState } from "react";
import api from "../services/api";
import type { Stock } from "../services/types";

function StocksPage() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [quantities, setQuantities] = useState<Record<string, number>>({});
  const [prices, setPrices] = useState<Record<string, number>>({});

  useEffect(() => {
    const fetchStocks = async () => {
      try {
        const res = await api.get("/api/stocks");
        if (res.data.success) {
          setStocks(res.data.data || []);
        } else {
          alert("Failed to get Stock List: " + res.data.message);
        }
      } catch (err) {
        const error = err as { response?: { data?: { message?: string } }, message?: string };
        alert("Failed to get Stock List: " + (error.response?.data?.message || error.message));
      }
    };

    void fetchStocks();
  }, []);

  const handleTrade = async (type: "buy" | "sell", symbol: string) => {
    try {
      const res = await api.post(`/api/trades/${type}`, {
        symbol,
        quantity: quantities[symbol] || 0,
        price: prices[symbol] || 0,
      });

      if (res.data.success) {
        alert(type === "buy" ? "Successfully bought" : "Successfully sold");
      } else {
        alert(`Failed to ${type}: ` + res.data.message);
      }
    } catch (err) {
      const error = err as { response?: { data?: { message?: string } }, message?: string };
      alert(`Failed to ${type}: ` + (error.response?.data?.message || error.message));
    }
  };

  return (
    <div>
      <h2>Stock List</h2>
      <div className="card">
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
              <th style={{ padding: "12px", textAlign: "left" }}>Code</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Name</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Price</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Quantity</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Trade Price</th>
              <th style={{ padding: "12px", textAlign: "left" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map((stock) => (
              <tr key={stock.symbol} style={{ borderBottom: "1px solid rgba(255,255,255,0.05)", height: "50px", verticalAlign: "middle" }}>
                <td style={{ padding: "12px", verticalAlign: "middle" }}><strong>{stock.symbol}</strong></td>
                <td style={{ padding: "12px", verticalAlign: "middle" }}>{stock.name}</td>
                <td style={{ padding: "12px", verticalAlign: "middle" }}>${stock.price?.toFixed(2)}</td>
                <td style={{ padding: "12px", verticalAlign: "middle" }}>
                  <input
                    type="number"
                    value={quantities[stock.symbol] || ""}
                    onChange={(e) => setQuantities({ ...quantities, [stock.symbol]: Number(e.target.value) })}
                    placeholder="Qty"
                    style={{ width: "70px", padding: "6px 8px" }}
                  />
                </td>
                <td style={{ padding: "12px", verticalAlign: "middle" }}>
                  <input
                    type="number"
                    value={prices[stock.symbol] || ""}
                    onChange={(e) => setPrices({ ...prices, [stock.symbol]: Number(e.target.value) })}
                    placeholder="Price"
                    style={{ width: "70px", padding: "6px 8px" }}
                  />
                </td>
                <td style={{ padding: "12px", verticalAlign: "middle" }}>
                  <button onClick={() => handleTrade("buy", stock.symbol)} className="primary" style={{ marginRight: "8px", padding: "6px 14px", fontSize: "0.9rem", minWidth: "60px" }}>Buy</button>
                  <button onClick={() => handleTrade("sell", stock.symbol)} style={{ background: "rgba(239,68,68,0.2)", color: "#ef4444", padding: "6px 14px", fontSize: "0.9rem", minWidth: "60px" }}>Sell</button>
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
