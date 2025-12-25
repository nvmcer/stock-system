📦 Stock System Backend — Spring Boot Service
🧭 Overview
This module is the core backend service of the Stock System.
It handles:
- Business logic
- Data validation
- Database read/write
- Integration with the FastAPI Market Data service
- Providing REST APIs to the frontend
This service is the only component that interacts with PostgreSQL.

🏗️ Architecture
┌──────────────────────────┐
│        Frontend          │
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
│        PostgreSQL        │
│  Database (5432)         │
└──────────────────────────┘

📁 Project Structure
stock-system-backend/
│
├── src/
│   ├── main/java/...        # Java source code
│   ├── main/resources/      # application.yml, static files
│   └── test/                # Unit tests
│
├── pom.xml                  # Maven configuration
└── README.md

🚀 Development
Start Backend (Dev Mode)
If running inside Docker Compose:
make dev

Or run locally:
mvn spring-boot:run

Backend will start at:
http://localhost:8080

🔧 Configuration
application.yml
  spring:
    datasource:
      url: jdbc:postgresql://postgres:5432/stockdb
      username: stockuser
      password: stockpassword
      driver-class-name: org.postgresql.Driver
    jpa:
      hibernate:
        ddl-auto: validate
      show-sql: true
      properties:
        hibernate:
          format_sql: true
    flyway:
      enabled: true
      locations: classpath:db/migration

🌐 API Endpoints
Example
GET /api/stocks
POST /api/stocks
GET /api/stocks/{id}

Market Data Integration
Backend calls FastAPI service:
GET http://stock-system-marketdata:8001/marketdata/{symbol}

Then:
- Processes the data
- Stores it in PostgreSQL
- Returns unified response to frontend

🐳 Docker (Dev)
Backend is included in docker-compose.dev.yml:
backend:
  build: ./stock-system-backend
  container_name: stock-system-backend-dev
  ports:
    - "8080:8080"
  volumes:
    - ./stock-system-backend:/app
  command: mvn spring-boot:run
  depends_on:
    - postgres

🏭 Docker (Prod)
Production build:
make prod


This will:
- Build JAR
- Build Docker image
- Run with optimized settings

🧪 Testing
Run tests:
mvn test