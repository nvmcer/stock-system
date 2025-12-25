📦 Stock System — Full Stack Architecture
🧭 Overview
A fully containerized multi-service stock management system built with:
- Frontend: Vite
- Backend: Spring Boot
- Market Data Service: FastAPI
- Database: PostgreSQL
- Orchestration: Docker Compose
- Automation: Makefile
This project is designed for clean architecture, easy onboarding, and production-ready deployment.

🏗️ System Architecture
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
│  → No DB access          │
└──────────────────────────┘

              │
              ▼
┌──────────────────────────┐
│        PostgreSQL        │
│        Database          │
│  ← Only accessed by      │
│     Spring Boot Backend  │
└──────────────────────────┘

📁 Project Structure
stock-system/
│
├── stock-system-frontend/       # Vite frontend
│   ├── src/
│   ├── public/
│   ├── .env.example
│   ├── vite.config.js
│   └── Dockerfile
│
├── stock-system-backend/        # Spring Boot backend
│   ├── src/main/java
│   ├── src/main/resources
│   ├── pom.xml
│   └── Dockerfile
│
├── stock-system-marketdata/     # FastAPI service
│   ├── app/
│   ├── requirements.txt
│   └── Dockerfile
│
├── docker-compose.dev.yml       # Dev environment
├── docker-compose.prod.yml      # Production environment
├── Makefile                     # Automation commands
└── README.md

🚀 Development Environment
Prerequisites
- Docker Desktop
- PowerShell + Scoop (for make)
- Git
Start Dev Environment
make dev

🏭 Production Build
make prod

This will:
- Build frontend static files
- Build backend JAR
- Build FastAPI image
- Start production docker-compose

🔧 Environment Variables
Frontend (.env.example)
VITE_MARKETDATA_API_URL=http://localhost:8001
VITE_API_BASE=http://localhost:8080
VITE_APP_ENV=development

Developers copy:
cp .env.example .env

🐳 Docker Services
Dev Mode
- Hot reload for frontend
- Hot reload for FastAPI
- Spring Boot dev mode
- Bind mounts for all services
Prod Mode
- Optimized images
- No bind mounts
- Static frontend served by backend or Nginx (optional)

🛠️ Makefile Commands
make dev       # Start dev environment
make prod      # Start production environment
make logs      # View logs
make down      # Stop all containers