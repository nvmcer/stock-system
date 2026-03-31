# Stock System Architecture and Production Preparation Report

Date: 2026-03-20

## 1. Current Position

The project is currently in a pure local-development stage.

What is already in place:

- frontend application
- backend application
- local PostgreSQL
- local observability stack
- Docker Compose-based development workflow

What is not yet in place:

- production infrastructure
- production deployment automation
- production runtime configuration
- production hardening baseline

Your target production architecture is:

- frontend: Cloudflare Pages
- backend: Hetzner VPS + Docker
- database: Neon PostgreSQL
- market data: Finnhub API

This report is organized around that target architecture rather than the deleted old infra path.

## 2. Current Architecture

### 2.1 Application Architecture

The current system flow is:

1. React SPA sends API requests.
2. Spring Boot API handles auth, stock operations, trades, portfolio calculations, and scheduled price updates.
3. PostgreSQL stores users, stocks, trades, and portfolio state.
4. Finnhub supplies market price data.

### 2.2 Local Development Runtime

The local runtime defined by the compose layers under [infra/compose/](/home/nvmcer/workspace/stock-system/infra/compose) includes:

- `frontend`
- `backend`
- `postgres`
- `loki`
- `grafana`
- `prometheus`
- `alloy`

That means local development is already service-complete, which is a strong base for moving toward production.

### 2.3 Backend Architecture

Backend stack and structure:

- Java 21
- Spring Boot 4.0.3
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- JWT authentication
- scheduled job for stock price updates
- Micrometer / Prometheus support
- structured JSON logging

Backend domain modules:

- `user`
- `stock`
- `trades`
- `portfolio`
- `security`
- `exception`
- `external/finnhub`

### 2.4 Frontend Architecture

Frontend stack:

- React 19
- TypeScript 5.9
- Vite 8
- React Router
- Axios

Frontend characteristics:

- SPA only
- route-based auth gating
- JWT stored in `localStorage`
- API base URL from `VITE_API_BASE`

### 2.5 Database Architecture

Current development DB:

- PostgreSQL 17.4 in Docker

Production target DB:

- Neon PostgreSQL

Schema management:

- Flyway migrations are present and already used

This is a good fit for migration because the app is already written against PostgreSQL and schema changes are versioned.

## 3. Target Production Architecture

### 3.1 Target Topology

Recommended production topology based on your goal:

1. User accesses frontend on Cloudflare Pages.
2. Frontend calls backend API over HTTPS.
3. Backend runs as a Docker container on Hetzner VPS.
4. Backend connects to Neon PostgreSQL over SSL.
5. Backend calls Finnhub API for stock data.
6. Scheduled price updates run inside the backend process, unless later moved to a separate scheduler.

### 3.2 Responsibility Split

Cloudflare Pages:

- static hosting
- CDN
- HTTPS termination for frontend

Hetzner VPS:

- backend runtime
- Docker
- reverse proxy
- process restarts
- server-level logs and metrics collection

Neon:

- managed PostgreSQL
- backups / branching capabilities from provider side

## 4. Gap Analysis Against Target Architecture

### 4.1 Frontend Gaps

Current status:

- frontend can already be built as a static SPA
- frontend production env still points to `http://localhost:8080`

Gap:

- production API base URL is not prepared
- deployment config for Cloudflare Pages is not prepared
- auth/session handling is still localStorage-based and minimal

### 4.2 Backend Gaps

Current status:

- backend has Dockerfile
- backend has prod profile
- backend supports JWT, scheduling, Flyway, logging, and metrics

Gap:

- production DB config is hardcoded to localhost
- JWT secret is hardcoded
- CORS config is hardcoded
- actuator endpoints are too open for production
- default admin bootstrap is unsafe for production
- some business APIs trust client-supplied `userId`

### 4.3 Database Gaps

Current status:

- PostgreSQL-compatible application code
- Flyway migrations exist

Gap:

- Neon connection settings are not wired
- SSL/TLS DB connection config is not defined
- backup/restore runbook is not documented
- migration / seed sequence for production is not documented

### 4.4 Deployment Gaps

Current status:

- local development is automated
- production deployment is not implemented yet

Gap:

- no VPS bootstrap
- no reverse proxy setup
- no container deployment procedure
- no CI/CD workflow
- no release/rollback process

### 4.5 Observability Gaps

Current status:

- app exposes health and Prometheus metrics
- local logging stack exists

Gap:

- no production observability design
- no production alerting
- no production metrics/log shipping path

## 5. Main Risks Before Production

### 5.1 Configuration Risk

Current production config files still point to localhost-based values. If unchanged, deployment will fail immediately.

### 5.2 Security Risk

Important risks observed:

- hardcoded JWT secret
- default admin credentials
- overly open actuator access
- hardcoded CORS allowlist

### 5.3 Authorization Risk

Several APIs accept `userId` directly from the client instead of binding behavior to the authenticated user. This is a production-blocking issue.

### 5.4 Operational Risk

There is no documented or automated production deployment path yet, so deployment would currently depend on manual steps.

### 5.5 Verification Risk

Testing coverage is still limited, especially around end-to-end critical business flows.

## 6. Production Readiness Conclusion

The project is technically suitable for production migration, but not ready for direct deployment yet.

Current readiness assessment:

- application architecture: ready enough
- local environment: strong
- production configuration: not ready
- production security: not ready
- production deployment: not ready
- production observability: partially ready in concept only

Overall:

- feasibility: high
- readiness now: low to medium
- best next step: execute a focused production-prep plan

## 7. Executable Task Checklist

This checklist is ordered to minimize rework.

### Phase A: Freeze the Production Design

- [ ] Confirm final production domain layout:
  Example: `app.example.com` for frontend, `api.example.com` for backend.
- [ ] Confirm Cloudflare Pages will host only the frontend static assets.
- [ ] Confirm Hetzner VPS will host only backend and reverse proxy.
- [ ] Confirm Neon will be the only production database.
- [ ] Decide whether scheduler stays inside Spring Boot or moves to external cron later.
- [ ] Write a short architecture decision note in the repo.

Deliverable:

- one page of agreed target production architecture

### Phase B: Clean and Prepare Configuration

- [ ] Change backend prod config to use environment variables for DB URL, DB user, DB password, and JWT secret.
- [ ] Add production-safe backend env variable documentation.
- [ ] Change frontend production config so `VITE_API_BASE` points to the real API domain at build time.
- [ ] Add `.env.production.example` or equivalent deployment env documentation.
- [ ] Move allowed CORS origins to configuration rather than hardcoded Java values.
- [ ] Decide how secrets will be injected on VPS.

Deliverable:

- app can boot in prod without editing source code

### Phase C: Fix Production-Blocking Security Issues

- [ ] Replace hardcoded JWT secret in backend code.
- [ ] Remove or redesign default admin auto-creation with fixed credentials.
- [ ] Restrict actuator endpoints in production.
- [ ] Review login/register rate limiting strategy.
- [ ] Remove legacy hardcoded frontend origins from CORS config.
- [ ] Decide whether frontend token strategy stays on `localStorage` for v1 or is upgraded later.

Deliverable:

- minimum safe security baseline for first release

### Phase D: Fix Authorization Model

- [ ] Refactor trade, portfolio, and history APIs so the backend derives the acting user from JWT/auth context.
- [ ] Remove reliance on client-supplied `userId` for normal user flows.
- [ ] Keep admin-only cross-user operations explicit and separately authorized if needed.
- [ ] Add tests for unauthorized cross-user access attempts.

Deliverable:

- user isolation is enforced server-side

### Phase E: Make Backend Production-Deployable

- [ ] Verify backend Docker image can run with prod env vars only.
- [ ] Add startup instructions for Flyway migration on first deploy.
- [ ] Confirm Neon SSL connection requirements and update JDBC parameters accordingly.
- [ ] Define container restart strategy.
- [ ] Decide how logs are collected on the VPS.
- [ ] Document backend run command / Compose file / systemd unit for production.

Deliverable:

- backend can run repeatably on a clean VPS

### Phase F: Make Frontend Production-Deployable

- [ ] Confirm `npm run build` outputs a static site ready for Cloudflare Pages.
- [ ] Configure Cloudflare Pages build command and output directory.
- [ ] Set production environment variable for API base URL in Cloudflare Pages.
- [ ] Verify SPA route fallback behavior in hosting setup.
- [ ] Test login, navigation, and API calls against a non-local API URL.

Deliverable:

- frontend can be published independently to Cloudflare Pages

### Phase G: Prepare Neon Database

- [ ] Create Neon project and production database.
- [ ] Generate production connection string.
- [ ] Verify network and SSL requirements.
- [ ] Test Flyway migrations against Neon.
- [ ] Define admin/bootstrap data creation procedure.
- [ ] Document restore and rollback expectations.

Deliverable:

- production database is ready before app deploy

### Phase H: Prepare Hetzner VPS Runtime

- [ ] Provision VPS with required CPU/RAM/disk sizing.
- [ ] Install Docker and Docker Compose or chosen runtime tooling.
- [ ] Install reverse proxy such as Nginx or Caddy.
- [ ] Configure HTTPS for `api` domain.
- [ ] Configure firewall rules to allow only required ports.
- [ ] Configure system updates and basic server hardening.
- [ ] Set up persistent log location or shipping.

Deliverable:

- backend host is production-capable

### Phase I: Add CI/CD and Release Safety

- [ ] Add backend test job in CI.
- [ ] Add frontend build/lint job in CI.
- [ ] Add Flyway validation in CI or pre-release checks.
- [ ] Add Docker image build pipeline for backend.
- [ ] Define deployment workflow for VPS.
- [ ] Define rollback procedure for bad releases.

Deliverable:

- releases are repeatable and low-friction

### Phase J: Add Minimum Production Observability

- [ ] Expose only required health endpoints publicly or behind control.
- [ ] Add uptime monitoring for backend API.
- [ ] Add alerts for app down, DB connection failure, and scheduler failure.
- [ ] Add a minimal metrics dashboard.
- [ ] Define log review path for incidents.

Deliverable:

- production can be monitored and supported

### Phase K: Staging-Like Dress Rehearsal

- [ ] Deploy to a staging-like environment or temporary VPS.
- [ ] Run migrations on fresh DB.
- [ ] Verify registration, login, stock listing, buy, sell, portfolio, and admin flows.
- [ ] Verify scheduler behavior and Finnhub integration.
- [ ] Verify frontend-to-backend CORS and HTTPS.
- [ ] Verify rollback procedure once before go-live.

Deliverable:

- first production deployment has already been rehearsed

## 8. Suggested Milestone Plan

### Milestone 1: Production Baseline

Scope:

- finalize architecture
- externalize config
- clean security basics

Definition of done:

- no hardcoded production secrets or localhost production config remain

### Milestone 2: Deployable Stack

Scope:

- Neon ready
- VPS ready
- backend and frontend independently deployable

Definition of done:

- app can be deployed end-to-end into a fresh environment

### Milestone 3: Safe Release

Scope:

- authz fixes
- CI checks
- observability
- rollback

Definition of done:

- release is reproducible and supportable

## 9. Immediate Top Priorities

If you only do the next 5 things, do these first:

1. Externalize backend production config and JWT secret.
2. Fix `userId`-based authorization design in user APIs.
3. Remove default admin hardcoded bootstrap behavior.
4. Prepare Neon-compatible DB connection and migration flow.
5. Decide and document the exact production deployment path for Cloudflare Pages + Hetzner VPS.

## 10. Verification Notes

This analysis is based on the current repo state, including:

- [README.md](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\README.md)
- [infra/compose/dev.yml](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\infra\compose\dev.yml)
- [apps/api/src/main/resources/application-prod.yml](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\apps\api\src\main\resources\application-prod.yml)
- [apps/api/src/main/java/com/config/SecurityConfig.java](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\apps\api\src\main\java\com\config\SecurityConfig.java)
- [apps/api/src/main/java/com/security/JwtUtil.java](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\apps\api\src\main\java\com\security\JwtUtil.java)
- [apps/web/.env.production](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\apps\web\.env.production)
- [apps/web/src/services/api.ts](\\wsl.localhost\Ubuntu\home\nvmcer\workspace\stock-system\apps\web\src\services\api.ts)

## 11. Final Recommendation

Do not start by writing all production infra at once.

The best sequence is:

1. lock the production design
2. fix application-level production blockers
3. prepare DB and runtime config
4. make backend and frontend independently deployable
5. then write deployment automation around that stable application baseline
