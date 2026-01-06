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

```mermaid
flowchart TB
 subgraph FE["Frontend (Vite Server 5173)"]
        FE1["Calls<br/>Backend API"]
  end
 subgraph BE["Backend (Spring Boot 8080)"]
        BE1["Business Logic"]
        BE2["Calls<br/>FastAPI"]
        BE3["Stores data<br/>in DB"]
  end
 subgraph FA["Market Data API (FastAPI 8001)"]
        FA1["Fetches<br/>External Data"]
        FA2["Returns Data<br/>to Backend"]
        FA3["No DB Access"]
  end
 subgraph DB["PostgreSQL Database"]
        DB1["Accessed only by<br/>Spring Boot Backend"]
  end
    FE1 L_FE1_BE1_0@--> BE1
    BE1 --> BE2 & BE3
    BE2 --> FA1
    FA1 --> FA2
    BE3 --> DB1

    FE1@{ shape: rect}
    BE1@{ shape: rect}
    BE2@{ shape: rect}
    BE3@{ shape: rect}
    FA1@{ shape: rect}
    FA2@{ shape: rect}
    FA3@{ shape: rect}
    DB1@{ shape: cyl}

    L_FE1_BE1_0@{ curve: natural }
```

📁 Project Structure
```
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
```

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
- Frontend (.env.example)
- VITE_MARKETDATA_API_URL=http://localhost:8001
- VITE_API_BASE=http://localhost:8080
- VITE_APP_ENV=development

Developers copy:
- cp .env.example .env

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
- make dev       # Start dev environment
- make prod      # Start production environment
- make logs      # View logs
- make down      # Stop all containers
