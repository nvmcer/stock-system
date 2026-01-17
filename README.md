📦 Stock System — Cloud Architecture

🧭 Overview

A fully cloud‑native, production‑ready stock management system deployed on AWS, built with a secure, scalable, and serverless‑first architecture.
This cloud version replaces local Docker Compose orchestration with managed AWS services, enabling high availability, automatic scaling, and infrastructure-as-code provisioning.

Core Components
- Frontend: React + Vite (hosted on S3, delivered via CloudFront CDN)
- Backend: Spring Boot API running on ECS Fargate
- Market Data Fetcher: AWS Lambda (scheduled stock data ingestion)
- Container Registry: Amazon ECR
- Database: Amazon RDS (PostgreSQL, private subnet only)
- Load Balancing: Application Load Balancer (ALB)
- Networking: VPC with public/private subnets, SG‑based isolation
- Infrastructure: Terraform (full IaC)

🏗️ System Architecture

```mermaid
flowchart TD
  %% ============================
  %% Frontend Layer
  %% ============================
  subgraph Frontend["Frontend"]
    Browser["User Browser"]
    CF["CloudFront<br>HTTPS + CDN"]
    S3["S3 Bucket<br>Static Site (React + Vite)"]
    Browser --> CF --> S3
  end

  %% ============================
  %% Backend Layer (Subnets)
  %% ============================
  subgraph PublicSubnet["Public Subnet"]
    ALB["Application Load Balancer<br>Listener 80/443"]
  end

  subgraph PrivateSubnet["Private Subnet"]
    ECS["ECS Service<br>Spring Boot API<br>Fargate + awsvpc<br>TargetGroup: IP"]
    ECR["ECR<br>Container Registry"]
    Lambda["Lambda<br>Scheduled Stock Fetch"]
    RDS["RDS PostgreSQL<br>Private Only"]
    Finnhub["Finnhub API<br>External Market Data"]
  end

  %% ============================
  %% Main Data Flows
  %% ============================
  S3 -->|API Call| ALB
  ALB -->|Forward to TargetGroup| ECS
  ECS -->|Write / Query| RDS
  ECS -->|Invoke or Receive Data| Lambda
  Lambda -->|Fetch Stock Data| Finnhub
  ECR --> ECS

  %% ============================
  %% Infrastructure Layer
  %% ============================
  subgraph Infrastructure["Infrastructure (Terraform Managed)"]
    VPC["VPC<br>ap-northeast-1"]
    SG["Security Groups"]
    IAM["IAM Roles"]
    TF["Terraform<br>Infrastructure as Code"]
  end

  %% ============================
  %% Infra Relationships
  %% ============================
  Infrastructure --- PublicSubnet
  Infrastructure --- PrivateSubnet

  SG -. Protects .- ALB
  SG -. Protects .- ECS
  SG -. Protects .- RDS

  IAM -. Assigned to .- ECS
  IAM -. Assigned to .- Lambda

  TF -. Manages .- VPC
  TF -. Manages .- ECS
  TF -. Manages .- ALB
  TF -. Manages .- Lambda
  TF -. Manages .- RDS
  TF -. Manages .- ECR

  %% ============================
  %% Styling
  %% ============================
  classDef infra fill:#F9F9F9,stroke:#bbb;
  class Infrastructure infra;
```

👉 Stock System — Full Stack Architecture

🔧 Local Development Environment

A dedicated local development environment is available in the dev branch.

It includes a Docker Compose–based architecture, service wiring, environment variables, and a separate architecture diagram tailored for local workflows.

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
