# Stock System - Market Data Service

This service provides real-time stock price data for the Stock System platform.  
It is built with **FastAPI** and integrates with **Yahoo Finance** to fetch the latest market prices.
---

## 🚀 Features
- Fetch real-time stock prices (AAPL, TSLA, etc.)
- Lightweight FastAPI microservice
- Designed to be consumed by the Spring Boot backend
- Easy to deploy locally or to cloud environments
---

## 📦 Requirements
- Python 3.10+
- FastAPI
- Uvicorn
- yfinance
Install dependencies:
　pip install -r requirements.txt
---

## ▶️ Running the Service
Start the FastAPI server:
uvicorn app.main:app --reload --port 8000
API will be available at:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc
---

## 📡 Example API Request
GET /prices?symbols=AAPL,TSLA
```json
Response:
{
  "AAPL": 192.3,
  "TSLA": 401.0
}
```
---
🧱 Project Structure
```
stock-system-marketdata/
│
├── app/
│   └── main.py
│
├── requirements.txt
└── README.md
```
---
🔗 Part of the Stock System

This service is one of the components of the larger Stock System:
- stock-system-frontend (React)
- stock-system-backend (Spring Boot)
- stock-system-marketdata (FastAPI) ← this repo
---