import { useState } from "react";
import PortfolioPage from "./PortfolioPage";
import StocksPage from "./StocksPage";
import TradesPage from "./TradesPage";

function UserDashboard() {
  const [activeTab, setActiveTab] = useState("portfolio");

  const renderContent = () => {
    switch (activeTab) {
      case "portfolio":
        return <PortfolioPage />;
      case "stocks":
        return <StocksPage />;
      case "trades":
        return <TradesPage />;
      default:
        return <PortfolioPage />;
    }
  };

  return (
    <div>
      <h1>Trading Dashboard</h1>
      <div className="card" style={{ padding: '0', marginBottom: '20px', overflow: 'hidden' }}>
        <nav style={{ display: 'flex', gap: '0', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
          <button 
            onClick={() => setActiveTab("portfolio")}
            style={{
              flex: 1,
              padding: '12px 16px',
              background: activeTab === 'portfolio' ? 'rgba(99,102,241,0.2)' : 'transparent',
              color: activeTab === 'portfolio' ? '#6366f1' : 'inherit',
              borderBottom: activeTab === 'portfolio' ? '2px solid #6366f1' : 'none',
              cursor: 'pointer',
              border: 'none',
              borderRadius: '0'
            }}
          >
            My Stock
          </button>
          <button 
            onClick={() => setActiveTab("stocks")}
            style={{
              flex: 1,
              padding: '12px 16px',
              background: activeTab === 'stocks' ? 'rgba(99,102,241,0.2)' : 'transparent',
              color: activeTab === 'stocks' ? '#6366f1' : 'inherit',
              borderBottom: activeTab === 'stocks' ? '2px solid #6366f1' : 'none',
              cursor: 'pointer',
              border: 'none',
              borderRadius: '0'
            }}
          >
            Stock List
          </button>
          <button 
            onClick={() => setActiveTab("trades")}
            style={{
              flex: 1,
              padding: '12px 16px',
              background: activeTab === 'trades' ? 'rgba(99,102,241,0.2)' : 'transparent',
              color: activeTab === 'trades' ? '#6366f1' : 'inherit',
              borderBottom: activeTab === 'trades' ? '2px solid #6366f1' : 'none',
              cursor: 'pointer',
              border: 'none',
              borderRadius: '0'
            }}
          >
            Trade History
          </button>
        </nav>
      </div>
      <div>{renderContent()}</div>
    </div>
  );
}

export default UserDashboard;