📦 Stock System Backend — Spring Boot Service

🧭 Overview
This module is the core backend service of the Stock System.
It handles:
- Business logic
- Data validation
- Database read/write
- Integration with Finnhub API for market data
- Providing REST APIs to the frontend
This service is the only component that interacts with PostgreSQL.
```
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
│  → Fetches Market Data   │
│  → Stores data in DB     │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────┐
│        PostgreSQL        │
│  Database (5432)         │
└──────────────────────────┘
```
---
📁 Project Structure
```
stock-system-backend/
│
├── src/
│   ├── main/java/...        # Java source code
│   ├── main/resources/      # application.yml, static files
│   └── test/                # Unit tests
│
├── pom.xml                  # Maven configuration
└── README.md
```
---
🚀 Development

Start Backend (Dev Mode)

If running inside Docker Compose:
- make dev

Or run locally:
- mvn spring-boot:run

Backend will start at:
- http://localhost:8080

---
🔧 Configuration
```
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
```
---
🌐 API Endpoints

Example
- GET /api/stocks
- POST /api/stocks
- GET /api/stocks/{id}

Market Data Integration

Backend integrates directly with Finnhub API:
- Real-time stock prices and company information
- Configured via FINNHUB_API_KEY environment variable
- Scheduled price updates via internal scheduler

Data flow:
- Fetches market data from Finnhub API
- Processes and validates the data
- Stores in PostgreSQL database
- Returns unified response to frontend
---
🐳 Docker (Dev)
Backend is included in docker-compose.dev.yml:
```
backend:
  image: eclipse-temurin:21-jdk
  container_name: stock-system-backend-dev
  working_dir: /app
  volumes:
    - ./stock-system-backend:/app
    - ~/.m2:/root/.m2
  command: ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8787"
  ports:
    - "8080:8080"
    - "8787:8787"
  environment:
    SPRING_PROFILES_ACTIVE: dev
    FINNHUB_API_KEY: ${FINNHUB_API_KEY}
  depends_on:
    - postgres
```
---
🏭 Docker (Prod)
Production build:
- make prod

This will:
- Build JAR
- Build Docker image
- Run with optimized settings
---
🧪 Testing

Run tests:
- mvn test