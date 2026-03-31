# STYLE_GUIDE.md - Stock System Code Standards

## Scope

This file is the single source of truth for code and implementation standards in this project.

If `AGENTS.md` and this file overlap, follow this file for all coding decisions.

## Architecture Baseline

- Frontend: React 19 + TypeScript + Vite
- Backend: Spring Boot 4 + Java 21
- Database: PostgreSQL
- Target production architecture: Cloudflare Pages + Hetzner VPS + Neon

## API Standards

All backend endpoints must use the shared response envelope:

```json
{
  "success": true,
  "code": "200",
  "message": "Operation successful",
  "data": {},
  "timestamp": "2026-03-30T00:00:00Z"
}
```

Rules:

- `success` is `true` for success and `false` for failure.
- `code` matches the HTTP status as a string.
- `message` is short and user-readable.
- `data` contains the payload or `null`.
- `timestamp` is ISO-8601 UTC.

## Backend Standards

### Structure

- Use constructor injection only.
- Keep controllers thin.
- Keep business logic in services.
- Keep database access in repositories.
- Centralize API error mapping in `GlobalExceptionHandler`.

### Data and Calculations

- Use `BigDecimal` for price, cost, and profit/loss values.
- Apply explicit rounding when returning calculated monetary values.

### Security

- Do not hardcode JWT secrets.
- Do not hardcode production CORS origins.
- Do not hardcode production database credentials.
- Do not trust client-supplied user identity for user-scoped operations.

### Database

- Every schema change requires a new Flyway migration.
- Never modify an applied migration.
- Migration path:
  - `apps/api/src/main/resources/db/migration/`

### Logging

- Use SLF4J.
- Log external API failures and business-significant failures.
- Do not log secrets, passwords, or tokens.

## Frontend Standards

### General

- Use explicit TypeScript interfaces or types.
- Avoid `any`.
- Keep API access in `src/services/`.
- Keep components and pages focused and readable.

### API Handling

- Read payloads from `response.data.data`.
- Handle unsuccessful API responses explicitly.
- Do not hardcode production API URLs in components.

### State

- Use local state for page-local UI.
- Introduce shared auth/global state only when it reduces duplication.

## Production Configuration Standards

- Frontend production API URL must come from `VITE_API_BASE`.
- Backend production settings must come from environment variables.
- Neon connections must require SSL.
- Production configuration files in git must be templates only, never live secrets.

## Observability Standards

- Backend must expose a health endpoint.
- Production actuator and metrics exposure must be stricter than local development.
- Structured backend logs are preferred for production runs.
