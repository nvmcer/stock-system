---
description: How to deploy the application to production
---

# Deploy Workflow

This workflow guides the deployment process for the stock system.

> **Note:** The production architecture is in transition. The deployment targets below reflect the planned stack. Update this workflow once the final architecture is decided.

## Planned Architecture
- **Frontend:** React SPA → Cloudflare Pages (auto-deploy from Git)
- **Backend:** Spring Boot → Docker → Hetzner VPS
- **Database:** Neon Serverless PostgreSQL
- **Infrastructure:** Terraform (IaC)

## Prerequisites
- Cloudflare account with Pages project configured.
- Hetzner VPS with Docker installed and SSH access.
- Neon project created with connection string.
- Terraform state backend configured.
- All tests passing locally.

## Steps

### 1. Pre-Deployment Checks

// turbo
#### Run Backend Tests
```bash
cd stock-system-backend && ./mvnw clean test
```

// turbo
#### Run Frontend Lint
```bash
cd stock-system-frontend && npm run lint
```

// turbo
#### Build Frontend
```bash
cd stock-system-frontend && npm run build
```

### 2. Frontend Deployment (Cloudflare Pages)

Cloudflare Pages can auto-deploy from Git on push to the main branch. Alternatively, deploy manually:

```bash
cd stock-system-frontend && npx wrangler pages deploy dist/ --project-name=stock-system
```

### 3. Backend Deployment (Hetzner VPS)

#### Build Docker Image
```bash
cd stock-system-backend && docker build -t stock-system-backend:latest .
```

#### Push to Container Registry
```bash
docker tag stock-system-backend:latest registry.example.com/stock-system-backend:latest
docker push registry.example.com/stock-system-backend:latest
```

#### Deploy on VPS (via SSH)
```bash
ssh user@hetzner-vps "docker pull registry.example.com/stock-system-backend:latest && docker compose -f docker-compose.prod.yml up -d"
```

### 4. Database (Neon)
- Flyway migrations run automatically on backend startup.
- Verify migration status in backend logs.
- Neon dashboard: https://console.neon.tech

### 5. Infrastructure Changes (if needed)

```bash
cd stock-system-infra
terraform plan
terraform apply
```

### 6. Post-Deployment Verification
- Verify backend health: `curl https://{api-domain}/actuator/health`
- Verify frontend loads in browser.
- Check Grafana dashboards for error spikes (if observability stack is deployed).
- Monitor backend logs for any issues.

## Rollback
- **Backend:** Redeploy previous Docker image tag.
- **Frontend:** Revert Git commit (Cloudflare auto-redeploys) or redeploy previous build.
- **Database:** Flyway does not support automatic rollback; prepare manual rollback SQL if needed.
