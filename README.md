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

🔧 Local Development Environment

A dedicated local development environment is available in the dev branch.

It includes a Docker Compose–based architecture, service wiring, environment variables, and a separate architecture diagram tailored for local workflows.

For full details, please refer to:

👉 dev branch — Local Development README
