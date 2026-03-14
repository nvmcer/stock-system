---
description: How to debug issues in the stock system
---

# Debug Workflow

This workflow provides a systematic approach to debugging issues in the stock system.

## Prerequisites
- Ensure dev environment is running: `make dev`
- Attach debugger if needed (see VSCode launch configs in `.vscode/launch.json`)

## Steps

### 1. Identify the Problem Layer
Determine which layer the issue is in:
- **Frontend** – UI rendering, state management, API calls
- **Backend** – Business logic, database queries, external API calls
- **Database** – Schema issues, data integrity, migration failures
- **Infrastructure** – Docker networking, environment variables, port mapping

### 2. Check Logs
// turbo
```bash
make logs
```
- Look for stack traces, error messages, and failed requests.
- Backend logs are in JSON format (Logstash encoder).

### 3. Frontend Debugging

#### Browser DevTools
- Open Chrome DevTools at `http://localhost:3001`
- Check Console tab for JavaScript errors.
- Check Network tab for failed API requests.
- Verify request/response payloads match `ApiResponse<T>` envelope.

#### VSCode Chrome Debugger
- Use the "Debug Frontend (Chrome)" launch configuration.
- Set breakpoints in `.tsx` files.

#### Common Frontend Issues
- CORS errors: Check `AppConfig.java` CORS configuration.
- 401 errors: Check JWT token in request headers (`Authorization: Bearer {token}`).
- Empty data: Verify `response.data.data` (double `.data` for Axios + envelope).

### 4. Backend Debugging

#### VSCode Java Debugger
- Start dev environment: `make dev` (debug port 8787 is already exposed).
- Use the "Debug Backend (Attach)" launch configuration.
- Set breakpoints in Java files.

#### Common Backend Issues
- 500 errors: Check `GlobalExceptionHandler` and backend logs.
- Database connection: Verify PostgreSQL container is running and credentials match.
- Flyway failures: Check migration file syntax and version ordering.
- Finnhub API: Verify `FINNHUB_API_KEY` is set in `.env`.

### 5. Database Debugging
```bash
docker exec -it stock-system-postgres-dev psql -U stockuser -d stockdb
```
- Check table schema: `\d table_name`
- Check migration history: `SELECT * FROM flyway_schema_history ORDER BY installed_rank;`
- Verify data: `SELECT * FROM table_name LIMIT 10;`

### 6. Docker/Network Debugging
// turbo
```bash
docker compose -f docker-compose.dev.yml ps
```
- Verify all containers are running and healthy.
- Check port mappings: Frontend (3001→5173), Backend (8080), PostgreSQL (5432), Debug (8787).

### 7. Environment Variables
- Backend: Check `application-dev.yml` and Docker Compose `environment` section.
- Frontend: Check `.env.development` and Vite env variable prefix (`VITE_`).
- Root: Check `.env` for `FINNHUB_API_KEY`.
