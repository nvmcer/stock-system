# Monorepo Environment Architecture

Date: 2026-04-05

## Goal

Use one codebase and one infrastructure model for `dev`, `test`, and `prod`.

Environment differences should be limited to:

- env values
- secrets
- deployment target
- small compose overrides

## Recommended Structure

```text
stock-system/
├── apps/
│   ├── web/
│   └── api/
├── infra/
│   ├── caddy/
│   ├── compose/
│   │   ├── base.yml
│   │   ├── dev.yml
│   │   ├── observability.yml
│   │   └── edge.yml
│   ├── env/
│   │   ├── dev/
│   │   ├── test/
│   │   └── prod/
│   ├── observability/
│   └── terraform/
│       ├── modules/
│       └── live/prod/
├── packages/
│   └── contracts/
├── docs/
└── Makefile
```

## Environment Model

### `dev`

- local Docker Compose
- local Postgres in Docker
- Vite dev server
- Spring Boot hot reload
- optional observability stack

Compose layers:

- `infra/compose/base.yml`
- `infra/compose/dev.yml`
- `infra/compose/observability.yml`

Config source:

- `infra/env/dev/compose.env`
- `infra/env/dev/api.env`
- `infra/env/dev/web.env`

Important dev constraints:

- `compose.env` should define `HOST_UID` and `HOST_GID` so the `api` and `web` containers write bind-mounted files as the host user.
- `api.env` must define `JWT_SECRET` with a value of at least 32 characters.
- Development admin bootstrap is opt-in only through `APP_ADMIN_BOOTSTRAP_ENABLED`, `APP_ADMIN_BOOTSTRAP_USERNAME`, and `APP_ADMIN_BOOTSTRAP_PASSWORD`.

### `test`

- Linux Surface server
- Docker + Caddy + Spring Boot API
- Neon test branch

Compose layers:

- `infra/compose/base.yml`
- `infra/compose/edge.yml`

Config source:

- `infra/env/test/compose.env.example`
- `infra/env/test/api.env.example`
- `infra/env/test/web.env.example`

### `prod`

- Cloudflare Pages for web
- Hetzner VPS for Caddy + Spring Boot API
- Neon prod branch

Compose layers:

- `infra/compose/base.yml`
- `infra/compose/edge.yml`

Config source:

- `infra/env/prod/compose.env.example`
- `infra/env/prod/api.env.example`
- `infra/env/prod/web.env.example`

## Core Rules

1. Do not create one full compose stack per environment.
2. Keep shared runtime in `base.yml`.
3. Keep local-only differences in `dev.yml`.
4. Keep edge/runtime differences in `edge.yml`.
5. Keep observability separate so it can be enabled only where useful.
6. Keep all environment contracts under `infra/env/`.

## Configuration Contract

### Compose runtime values

Examples:

- `COMPOSE_PROJECT_NAME`
- `API_IMAGE`
- `API_ENV_FILE`
- `CADDYFILE_PATH`
- `CADDY_SITE_ADDRESS`
- `CADDY_HTTP_PORT`
- `CADDY_HTTPS_PORT`
- `API_UPSTREAM`
- `HOST_UID`
- `HOST_GID`

### API runtime values

Examples:

- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `FINNHUB_API_KEY`
- `CORS_ALLOWED_ORIGINS`
- `APP_ADMIN_BOOTSTRAP_ENABLED`
- `APP_ADMIN_BOOTSTRAP_USERNAME`
- `APP_ADMIN_BOOTSTRAP_PASSWORD`

### Web build-time values

Examples:

- `VITE_API_BASE`
- `VITE_APP_ENV`

## Security and Access Rules

- User-scoped API operations must derive the acting user from the JWT/authentication context, and newly issued JWTs should carry the authenticated `userId` claim to avoid per-request lookup overhead.
- Client-provided `userId` is only valid for explicit admin flows.
- `POST /api/stocks/update-prices` is restricted to admins.
- Only `/actuator/health` should be public. All other actuator endpoints should require admin privileges in every environment, including `dev`, because local stacks may still be reachable on shared networks.

## Secrets Strategy

Git tracks only:

- local dev defaults
- `*.example` files
- compose templates
- caddy templates

Secrets should live in:

- GitHub environment secrets for `test` and `prod`
- server runtime files outside git
- Cloudflare Pages environment variables for web build values

## Deployment Flow

### Dev

```bash
docker compose \
  --env-file infra/env/dev/compose.env \
  -f infra/compose/base.yml \
  -f infra/compose/dev.yml \
  -f infra/compose/observability.yml \
  up --build
```

### Test

```bash
docker compose \
  --env-file /opt/stock-system/test/compose.env \
  -f /opt/stock-system/test/base.yml \
  -f /opt/stock-system/test/edge.yml \
  up -d
```

### Prod

```bash
docker compose \
  --env-file /opt/stock-system/prod/compose.env \
  -f /opt/stock-system/prod/base.yml \
  -f /opt/stock-system/prod/edge.yml \
  up -d
```

## CI/CD Recommendation

Use one workflow that:

1. tests `apps/web` and `apps/api`
2. builds one API image artifact
3. deploys that image to `test`
4. promotes the same image to `prod`

## Terraform Recommendation

Only `prod` needs Terraform right now.

Recommended split:

- `infra/terraform/live/prod/global`: Cloudflare Pages and DNS
- `infra/terraform/live/prod/app`: Hetzner VM and firewall

This keeps `prod` ready for future expansion without forcing Terraform onto `dev` or `test` too early.

## Bottom Line

Best practice here is not “three sets of infra”.

It is:

- one `apps/` layer
- one `infra/` layer
- one `packages/` layer for shared contracts
- one env contract under `infra/env/`
- one shared compose model with small overrides
