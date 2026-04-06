# AGENTS.md - Stock System AI Instructions

## Workflow (MANDATORY)

This repository follows [AI_WORKFLOW.md](./docs/AI_WORKFLOW.md).

It is the **single source of truth** for how all tasks must be executed.

---

## Hard Rules

Before making any code changes, you MUST:

1. Read `AI_WORKFLOW.md`
2. Create:
   - `docs/<feature-name>/spec.md`
   - `docs/<feature-name>/tasks.md`
3. Work in:
   - `feature/<feature-name>` branch or worktree
4. Execute tasks from `tasks.md`
5. Run tests before completion
6. Prepare PR with references to spec and tasks

---

## Forbidden

- Coding without spec.md
- Skipping tasks.md
- Editing main branch directly
- Ignoring failing tests

---

## Purpose

This file is for AI agents only. It defines project-specific constraints, coupling points, and high-risk areas.

For all code standards, use [STYLE_GUIDE.md](./docs/STYLE_GUIDE.md) as the single source of truth.

## Project Structure

- `apps/web/` - React SPA
- `apps/api/` - Spring Boot API
- `infra/` - shared docker, caddy, env, observability, and terraform assets
- `docs/` - architecture, migration, and deployment docs
- `packages/` - shared contracts and future cross-app packages

## Target Production Architecture

- Frontend: Cloudflare Pages
- Backend: Hetzner VPS + Docker
- Reverse proxy: VPS edge
- Database: Neon PostgreSQL
- External market data: Finnhub

Prefer this target architecture for all production-facing changes.

## Core System Flow

```text
Frontend -> Backend API -> PostgreSQL
                  |
                  -> Finnhub
```

## Shared Contracts

- API response envelope
  - Frontend: `apps/web/src/services/api.ts`
  - Backend: `apps/api/src/main/java/com/exception/ApiResponse.java`
- Stock DTOs
  - Backend: `apps/api/src/main/java/com/stock/dto/`
  - Frontend consumers: stock-related pages and types
- Trade DTOs
  - Backend: `apps/api/src/main/java/com/trades/dto/`
  - Frontend consumers: trade-related pages and types
- Portfolio DTOs
  - Backend: `apps/api/src/main/java/com/portfolio/dto/`
  - Frontend consumers: portfolio-related pages and types
- Auth/JWT
  - Backend: `apps/api/src/main/java/com/security/`
  - Frontend consumers: login flow, auth storage, authenticated API calls

## Required Change Coupling

- If backend DTOs change, update the frontend pages/types that consume them.
- If the API envelope changes, update backend exception handling and frontend response parsing together.
- If auth changes, update backend security and frontend login/request handling together.
- If schema changes, add a new Flyway migration.
- If production config changes, keep backend env vars, frontend env vars, and deployment docs aligned.

## High-Risk Areas

- User-scoped endpoints must not trust client-supplied `userId`.
- Production secrets, hosts, and credentials must not be hardcoded.
- Frontend production API URLs must come from environment config.
- Backend production DB connections must target Neon-compatible PostgreSQL with SSL.
