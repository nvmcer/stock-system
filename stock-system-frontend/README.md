📦 Stock System Frontend — Vite Application
🧭 Overview
This module is the frontend application of the Stock System.
It provides:
- A modern UI built with Vite
- Integration with the Spring Boot backend
- Integration with the FastAPI market data service (via backend)
- Hot-reload development environment
- Production-ready static build
The frontend does not access the database directly.
All data flows through the backend.

🏗️ Architecture
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
│  → Calls FastAPI         │
│  → Stores data in DB     │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────┐
│     Market Data API      │
│       FastAPI (8001)     │
│  → Fetches external data │
│  → Returns to Backend    │
└──────────────────────────┘

📁 Project Structure
stock-system-frontend/
│
├── src/                    # Frontend source code
│   ├── components/
│   ├── pages/
│   ├── api/
│   └── main.js / main.ts
│
├── public/                 # Static assets
├── .env.example            # Environment variable template
├── package.json
├── vite.config.js
└── README.md

🚀 Development
Start Dev Server
If running inside Docker Compose:
make dev

Or run locally:
npm install
npm run dev

Frontend will start at:
http://localhost:3001

(Proxying to Vite’s internal port 5173)

🔧 Environment Variables
The frontend uses Vite’s environment system.
.env.example
VITE_MARKETDATA_API_URL=http://localhost:8001
VITE_API_BASE=http://localhost:8080
VITE_APP_ENV=development

Developers copy:
cp .env.example .env

🏭 Production Build
Build static files:
npm run build

Preview:
npm run preview

In production Docker mode, the frontend is built and served by the backend or Nginx (depending on your setup).

📡 API Integration
Backend API
All business logic and DB operations go through:
VITE_BACKEND_API_URL

Example:
const res = await fetch(`${import.meta.env.VITE_BACKEND_API_URL}/api/stocks`);

Market Data API
Frontend does not call FastAPI directly in your architecture.
Backend handles:
- Fetching market data
- Processing
- Storing
- Returning unified responses

🧪 Testing
(If you add tests later)
npm run test

🐳 Docker
Included in docker-compose.dev.yml:
frontend:
  build: ./stock-system-frontend
  container_name: stock-system-frontend-dev
  ports:
    - "3001:5173"
  volumes:
    - ./stock-system-frontend:/app
  command: npm run dev -- --host