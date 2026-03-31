# API App

Spring Boot API for the stock system.

## Responsibilities

- authentication and authorization
- stock, trade, and portfolio APIs
- PostgreSQL persistence
- Finnhub integration
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

## Tests

```bash
./mvnw test
```
