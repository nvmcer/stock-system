# Stock System

Stock portfolio system built as a monorepo with a React web app, a Spring Boot API, and shared infrastructure for `dev`, `test`, and `prod`.

## Monorepo Layout

```text
stock-system/
├── apps/
│   ├── web/
│   └── api/
├── infra/
│   ├── caddy/
│   ├── compose/
│   ├── env/
│   ├── observability/
│   └── terraform/
├── packages/
│   └── contracts/
├── docs/
├── AGENTS.md
├── Makefile
└── README.md
```

## Environment Strategy

- `dev`: full Docker Compose on the local machine with hot reload and observability
- `test`: Surface Linux server with Docker, Caddy, Spring Boot API, and Neon test branch
- `prod`: Cloudflare Pages for web, Hetzner VPS for Caddy and Spring Boot API, Neon prod branch

The repo uses one shared infrastructure model:

- `infra/compose/base.yml`
- `infra/compose/dev.yml`
- `infra/compose/observability.yml`
- `infra/compose/edge.yml`

Environment differences live in:

- `infra/env/dev/`
- `infra/env/test/`
- `infra/env/prod/`

## Development

Start the full local stack:

```bash
make dev
```

Start the local stack without observability:

```bash
make dev-lite
```

Useful commands:

- `make dev`
- `make dev-d`
- `make dev-down`
- `make logs`

## Production Target

- web: Cloudflare Pages
- API: Hetzner VPS + Docker
- reverse proxy: Caddy
- database: Neon PostgreSQL
- market data: Finnhub

See:

- `docs/MONOREPO_ENV_ARCHITECTURE.md`
- `docs/migration/PRODUCTION_DEPLOYMENT_TASKLIST.md`
- `docs/migration/ARCHITECTURE_AND_MIGRATION_REPORT_2026-03-20.md`
