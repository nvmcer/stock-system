# Production Deployment Task List

Date: 2026-03-30
Target architecture:

- Frontend: Cloudflare Pages
- Backend: Hetzner VPS + Docker
- Reverse proxy: Caddy
- Database: Neon PostgreSQL
- External API: Finnhub

This document is the execution path for the first production deployment. Each section explains:

- what to do
- why it matters
- what success looks like

## 1. Freeze the Production Topology

### What to do

- Decide the public domains:
  - `app.yourdomain.com` for the frontend
  - `api.yourdomain.com` for the backend
- Confirm the responsibility split:
  - Cloudflare Pages serves only frontend static assets
  - Hetzner VPS runs only backend and reverse proxy
  - Neon hosts the production PostgreSQL database

### Why

Every later step depends on stable domains and stable ownership boundaries. If you change these late, you will redo CORS, DNS, TLS, environment variables, and deployment scripts.

### Success looks like

- the final frontend domain is fixed
- the final backend domain is fixed
- there is no ambiguity about where each service runs

## 2. Prepare Production Secrets and Runtime Values

### What to do

Prepare these values before deployment:

- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `FINNHUB_API_KEY`
- `CORS_ALLOWED_ORIGINS`
- `VITE_API_BASE`

Recommended target values:

```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://<neon-host>/<db>?sslmode=require
DATABASE_USERNAME=<neon-user>
DATABASE_PASSWORD=<neon-password>
JWT_SECRET=<long-random-secret>
FINNHUB_API_KEY=<your-key>
CORS_ALLOWED_ORIGINS=https://app.yourdomain.com
VITE_API_BASE=https://api.yourdomain.com
```

### Why

Production deployment fails early if values are missing, and production security fails silently if secrets remain hardcoded in source.

### Success looks like

- every production value is known
- no secret needs to be typed manually during go-live

## 3. Update the Backend for Production Configuration

### What to do

Modify the backend so production works through environment variables only.

Required changes:

- [ ] Replace localhost DB values in [application-prod.yml](/home/nvmcer/workspace/stock-system/apps/api/src/main/resources/application-prod.yml)
- [ ] Load JWT secret from configuration instead of hardcoding it in [JwtUtil.java](/home/nvmcer/workspace/stock-system/apps/api/src/main/java/com/security/JwtUtil.java)
- [ ] Load allowed CORS origins from configuration in [SecurityConfig.java](/home/nvmcer/workspace/stock-system/apps/api/src/main/java/com/config/SecurityConfig.java)
- [ ] Restrict actuator exposure in production
- [ ] Remove the fixed default-admin bootstrap or replace it with a one-time controlled setup

Suggested backend production config shape:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

### Why

The backend is currently tied to local assumptions. Production needs runtime configuration because:

- the database is Neon, not local Postgres
- the frontend origin is a real domain, not localhost
- JWT signing must use a private secret

### Success looks like

- backend can start in `prod` with only env vars
- no production host, secret, or password is hardcoded

## 4. Fix User Authorization Before Deployment

### What to do

Refactor user-scoped APIs so they derive the acting user from JWT/auth context instead of trusting `userId` from request parameters.

At minimum:

- [ ] portfolio query
- [ ] trade buy
- [ ] trade sell
- [ ] trade history

Add tests that prove one user cannot act on another user’s data.

### Why

This is a production safety issue, not just a cleanup task. If a user can submit another user’s ID, the system can leak data or allow unauthorized operations.

### Success looks like

- regular users can only access their own resources
- cross-user access requires explicit admin logic

## 5. Prepare the Frontend for Cloudflare Pages

### What to do

Update the frontend production settings:

- [ ] Set [apps/web/.env.production](/home/nvmcer/workspace/stock-system/apps/web/.env.production) to `VITE_API_BASE=https://api.yourdomain.com`
- [ ] Make sure `npm run build` produces a valid `dist/`
- [ ] Verify there are no remaining localhost API assumptions

### Why

Cloudflare Pages is a static host. It will build and serve the frontend, but the frontend must already know where the real backend lives.

### Success looks like

- the frontend build references the production API domain
- the built app does not call localhost anywhere

## 6. Create the Neon Production Database

### What to do

In Neon:

- [ ] Create a project
- [ ] Create the production database
- [ ] Create a production DB user/role
- [ ] Copy the production connection details
- [ ] Confirm SSL is required

Then wire the backend to Neon using JDBC:

```env
DATABASE_URL=jdbc:postgresql://<neon-host>/<db>?sslmode=require
DATABASE_USERNAME=<neon-user>
DATABASE_PASSWORD=<neon-password>
```

After that:

- [ ] Run Flyway migrations against Neon
- [ ] Verify the schema is created correctly

### Why

Neon replaces the local Docker Postgres in production. Flyway must succeed there before the app is usable.

### Success looks like

- backend can connect to Neon
- Flyway initializes the schema
- the app no longer depends on local Postgres assumptions

## 7. Create and Harden the Hetzner VPS

### What to do

Create a Cloud server with:

- Ubuntu 24.04 LTS
- 2 vCPU / 4 GB RAM as a reasonable first production size
- your SSH key attached

Then:

- [ ] create a non-root sudo user
- [ ] update packages
- [ ] restrict SSH access
- [ ] attach a firewall

Recommended firewall:

- allow TCP 22 from your admin IP only
- allow TCP 80 from anywhere
- allow TCP 443 from anywhere

### Why

This VPS is the public backend host. It must be reachable, secure enough for first production use, and predictable to manage.

### Success looks like

- you can log in by SSH key
- only necessary ports are public
- the server is ready for Docker workloads

## 8. Install Docker and Prepare the Runtime Directory

### What to do

Install:

- Docker Engine
- Docker Compose plugin

Create a production runtime directory such as:

```text
/opt/stock-system/backend/
  api.env
  compose.env
  base.yml
  edge.yml
  caddy/Caddyfile.prod
```

Recommended repo source files:

- `/home/nvmcer/workspace/stock-system/infra/compose/base.yml`
- `/home/nvmcer/workspace/stock-system/infra/compose/edge.yml`
- `/home/nvmcer/workspace/stock-system/infra/caddy/Caddyfile.prod`
- `/home/nvmcer/workspace/stock-system/infra/env/prod/compose.env.example`
- `/home/nvmcer/workspace/stock-system/infra/env/prod/api.env.example`

### Why

Keeping runtime files in one stable directory makes deployment, rollback, and maintenance simpler. It also gives you one place to inspect the live backend configuration.

### Success looks like

- Docker works
- `/opt/stock-system/backend` exists and is the only runtime directory you need

## 9. Create the Backend Production `.env`

### What to do

On the VPS, create:

- `/opt/stock-system/backend/api.env`

Example:

```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://<neon-host>/<db>?sslmode=require
DATABASE_USERNAME=<neon-user>
DATABASE_PASSWORD=<neon-password>
JWT_SECRET=<long-random-secret>
FINNHUB_API_KEY=<finnhub-key>
CORS_ALLOWED_ORIGINS=https://app.yourdomain.com
SERVER_PORT=8080
```

Set file permissions to owner-only.

### Why

This file is the source of runtime truth for the live backend. It decouples secrets from source code and keeps deployment reproducible.

### Success looks like

- backend secrets are present only on the server
- file permissions are restricted

## 10. Create the Production Compose File

### What to do

Copy `/home/nvmcer/workspace/stock-system/infra/compose/base.yml` and `/home/nvmcer/workspace/stock-system/infra/compose/edge.yml` to `/opt/stock-system/backend/`.

Recommended structure:

```yaml
services:
  api:
    image: your-registry/stock-system-api:latest
    env_file:
      - ./api.env
    restart: unless-stopped
    expose:
      - "8080"

  caddy:
    image: caddy:2.10-alpine
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile
      - caddy_data:/data
      - caddy_config:/config
    depends_on:
      - api

volumes:
  caddy_data:
  caddy_config:
```

### Why

Compose gives you a simple first production runtime:

- backend container
- reverse proxy container
- restart policy
- one command to start and inspect the stack

### Success looks like

- `docker compose --env-file compose.env -f base.yml -f edge.yml up -d` starts both services

## 11. Configure Caddy as the Reverse Proxy

### What to do

Copy `/home/nvmcer/workspace/stock-system/infra/caddy/Caddyfile.prod` to `/opt/stock-system/backend/caddy/Caddyfile.prod`.

```caddy
api.yourdomain.com {
    encode gzip
    reverse_proxy api:8080
}
```

Then start the stack after DNS points to the server.

### Why

Caddy gives you:

- reverse proxying to the backend container
- automatic HTTPS certificates
- simpler configuration than Nginx for a first production deployment

### Success looks like

- `https://api.yourdomain.com` returns backend responses
- TLS is valid without manual certificate handling

## 12. Configure Cloudflare DNS for the Backend

### What to do

In Cloudflare DNS:

- [ ] create `A` record for `api` -> VPS IPv4
- [ ] create `AAAA` record too if using IPv6
- [ ] start in DNS-only mode if you want easier origin debugging

### Why

The backend domain must resolve to the VPS before Caddy can serve HTTPS for that hostname.

### Success looks like

- `api.yourdomain.com` resolves to the VPS
- Caddy can obtain certificates successfully

## 13. Configure Cloudflare Pages for the Frontend

### What to do

Create a Cloudflare Pages project and connect the repository.

Recommended settings:

- Root directory: `apps/web`
- Build command: `npm run build`
- Output directory: `dist`
- Environment variable:
  - `VITE_API_BASE=https://api.yourdomain.com`

### Why

Cloudflare Pages needs the project root and build output to know how to build the SPA. It also needs the production API URL at build time.

### Success looks like

- the Pages build succeeds
- the preview build loads correctly
- the frontend talks to the real API domain

## 14. Attach the Frontend Production Domain

### What to do

In Cloudflare Pages:

- [ ] add `app.yourdomain.com` as a custom domain
- [ ] follow the DNS instructions Cloudflare gives
- [ ] wait until the domain becomes active

### Why

The custom domain is the stable production entrypoint for users. It also becomes the exact frontend origin that the backend CORS policy should allow.

### Success looks like

- `https://app.yourdomain.com` serves the production frontend

## 15. Run the First Backend Deployment

### What to do

On the VPS:

- [ ] place the production backend env file
- [ ] place `compose.env`
- [ ] place `base.yml`
- [ ] place `edge.yml`
- [ ] place `caddy/Caddyfile.prod`
- [ ] pull or build the backend image
- [ ] run:

```bash
docker compose --env-file compose.env -f base.yml -f edge.yml up -d
docker compose --env-file compose.env -f base.yml -f edge.yml logs -f
```

### Why

This is the first full integration point between:

- Hetzner runtime
- Neon database
- Caddy reverse proxy
- backend application

### Success looks like

- backend container starts
- Flyway succeeds
- Caddy starts and serves the API hostname

## 16. Verify the Whole Production Path

### What to do

Check in this order:

1. backend health endpoint
2. frontend load
3. login
4. stock list
5. buy/sell
6. portfolio
7. admin features
8. scheduled update behavior

Minimum checks:

- [ ] `https://api.yourdomain.com/actuator/health`
- [ ] `https://app.yourdomain.com`
- [ ] browser devtools show API calls to `https://api.yourdomain.com`
- [ ] no mixed-content issues
- [ ] no CORS issues

### Why

A successful deploy is not just “containers are up.” It means the user flow works across DNS, TLS, browser, backend, and database.

### Success looks like

- the application works end to end from the public domains

## 17. Add Minimal Post-Deploy Safety

### What to do

After first success:

- [ ] tighten production actuator exposure
- [ ] document rollback steps
- [ ] document how to rotate DB password and JWT secret
- [ ] document how to redeploy the backend
- [ ] define how logs are inspected on the server

### Why

The first production release is only the beginning. These notes prevent later incidents from turning into guesswork.

### Success looks like

- you can recover, redeploy, and inspect the system without improvising

## 18. Recommended Execution Order Summary

Do the work in this order:

1. freeze domains and topology
2. externalize backend production config
3. fix user authorization
4. prepare frontend production API config
5. create Neon database
6. create and harden Hetzner VPS
7. install Docker and prepare runtime files
8. configure Caddy and DNS
9. deploy backend
10. deploy frontend on Cloudflare Pages
11. run end-to-end validation
12. add post-deploy safety notes
