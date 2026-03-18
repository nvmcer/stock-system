📦 Stock System Frontend — Vite Application

🧭 Overview
This module is the frontend application of the Stock System.
It provides:
- A modern UI built with Vite
- Integration with the Spring Boot backend
- Integration with Finnhub market data (via backend)
- Hot-reload development environment
- Production-ready static build
The frontend does not access the database directly.
All data flows through the backend.
---

🏗️ Architecture
```
┌──────────────────────────┐
│        Frontend          │
│  Vite Dev Server (5173)  │
│  → Calls Backend API     │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────┐
│        Backend           │
│     Spring Boot (8080)   │
│  → Business Logic        │
│  → Fetches Market Data   │
│  → Stores data in DB     │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────┐
│     External APIs        │
│       Finnhub API        │
│  → Real-time stock data  │
│  → Company information   │
└──────────────────────────┘
```
---
📁 Project Structure
```
stock-system-frontend/
│
├── src/                    # Frontend source code
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── assets/
│   ├── App.tsx
│   ├── App.css
│   ├── index.css
│   └── main.tsx
│
├── public/                 # Static assets
├── .env.example            # Environment variable template
├── package.json
├── vite.config.ts
└── README.md
```
---
🚀 Development
Start Dev Server

If running inside Docker Compose:
- make dev

Or run locally:
- npm install
- npm run dev

Frontend will start at:
- http://localhost:3001

(Proxying to Vite’s internal port 5173)

---
🔧 Environment Variables
The frontend uses Vite’s environment system.

.env.example
- VITE_API_BASE = http://localhost:8080
- VITE_APP_ENV = development

Developers copy:

cp .env.example .env

---
🏭 Production Build

Build static files:
- npm run build

Preview:
- npm run preview

In production Docker mode, the frontend is built and served by the backend or Nginx (depending on your setup).

---
📡 API Integration

Backend API

All business logic and DB operations go through:
- VITE_API_BASE (e.g., http://localhost:8080)

Example:

const res = await fetch(`${import.meta.env.VITE_API_BASE}/api/stocks`);

Market Data Integration

Frontend does not call external APIs directly. All market data is fetched by the backend via Finnhub API.

Backend handles:
- Fetching real-time market data from Finnhub
- Processing and validation
- Storing in database
- Returning unified API responses

---
🧪 Testing
(If you add tests later)
npm run test

---
🐳 Docker

Included in docker-compose.dev.yml:
```
frontend:
  image: node:25
  container_name: stock-system-frontend-dev
  working_dir: /app
  volumes:
    - ./stock-system-frontend:/app
  command: >
    sh -c "npm install &&
          npm run dev -- --host"
  ports:
    - "3001:5173"
  depends_on:
    - backend
```
---