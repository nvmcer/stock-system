# API App

Spring Boot API for the stock system.

## Responsibilities

- authentication and authorization
- stock, trade, and portfolio APIs
- portfolio AI report generation and latest-report retrieval
- PostgreSQL persistence
- Finnhub integration
- user-triggered OpenAI-compatible provider integration for portfolio analysis
- scheduled price updates

## Location

```text
apps/api/
├── src/
├── pom.xml
├── Dockerfile
└── README.md
```

## Local Development

Run through the monorepo compose stack:

```bash
make dev
```

Or run directly:

```bash
./mvnw spring-boot:run
```

The default local port is `8080`.

## Runtime Configuration

The API reads runtime values from env files managed under `infra/env/`.

Examples:

- `infra/env/dev/api.env`
- `infra/env/test/api.env.example`
- `infra/env/prod/api.env.example`

### Required Configuration

- `JWT_SECRET` is required in every environment and must be at least 32 characters.
- `CORS_ALLOWED_ORIGINS` should list the allowed frontend origins for `test` and `prod`.
- In `dev`, CORS defaults to remote-friendly origin patterns for localhost, LAN, and common remote-dev IP ranges. Override them with `CORS_DEV_ALLOWED_ORIGIN_PATTERNS` only if you need something narrower or broader.
- `FINNHUB_API_KEY` is optional for local development but required for real market-data refreshes. Finnhub quote fetches now use an explicit optional result internally so missing quotes are handled without null-based control flow.
- No shared server-side LLM API key is required for the portfolio report flow. Users provide OpenAI-compatible API keys per request, but the API runtime still needs outbound HTTPS access to the selected provider.

### Admin Bootstrap

Development-only admin bootstrap is disabled by default.

To enable it intentionally in `dev`, set:

- `APP_ADMIN_BOOTSTRAP_ENABLED=true`
- `APP_ADMIN_BOOTSTRAP_USERNAME=<username>`
- `APP_ADMIN_BOOTSTRAP_PASSWORD=<password>`

Do not enable this flow in `test` or `prod`.

## Security Model

- User-scoped endpoints resolve the acting user from the authenticated JWT context. JWTs include `role` and `userId` claims so the backend can resolve the current user without an extra lookup for newly issued tokens.
- Regular users must not be able to act on another user's portfolio or trade history by supplying a different `userId`.
- The latest AI portfolio report is stored per user in PostgreSQL, while provider API keys remain transient and must never be persisted or logged.
- `POST /api/stocks/update-prices` is admin-only.
- Only `/actuator/health` is public. All other actuator endpoints require an admin identity, including in `dev`, while still allowing Basic auth on the dedicated actuator filter chain.

## Portfolio AI Report Endpoints

- `POST /api/portfolio/analysis-report`: generate and persist the latest user report
- `GET /api/portfolio/analysis-report/latest`: fetch the latest persisted report for the acting user

## Tests

```bash
./mvnw test
```
