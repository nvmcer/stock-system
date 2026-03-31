export interface PortfolioItem {
  symbol: string;
  name: string;
  quantity: number;
  avgCost: number;
  currentPrice: number;
  profitLoss: number;
  realizedProfit: number;
  unrealizedProfit: number;
  totalProfit: number;
}

export interface Stock {
  id: number;
  symbol: string;
  name: string;
  price: number;
  lastUpdated: string;
}

export interface Trade {
  id: number;
  userId: number;
  symbol: string;
  type: "BUY" | "SELL";
  quantity: number;
  price: number;
  totalAmount: number;
  timestamp: string;
  stockSymbol: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}