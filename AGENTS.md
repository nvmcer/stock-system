# AGENTS.md – Stock System Development Guidelines

Build commands, code style, and conventions for agentic coding assistants.

## Project Structure

- `stock-system-backend/` – Spring Boot Java backend
- `stock-system-frontend/` – React + TypeScript + Vite frontend
- `stock-system-marketdata/` – FastAPI Python service (AWS Lambda)
- `stock-system-infra/` – Terraform infrastructure as code
- Root `Makefile` – Common automation commands

## Architect
- Role: Senior System Architect
- Capability: Project analysis, standard setting, consistency checking.
- Instructions: Always check `STYLE_GUIDE.md` before suggesting code changes.

## Refactor_Bot
- Role: Code Refactoring Specialist
- Capability: Applying patterns, adding Javadoc/Type hints, fixing anti-patterns.

## Build, Lint, and Test Commands

### Backend (Java/Spring Boot)

**Build:** `./mvnw clean package` (or `-DskipTests`)  
**Run tests:** `./mvnw test`  
**Single test:** `./mvnw test -Dtest=ClassName`  
**Run locally:** `./mvnw spring-boot:run`  
**Code quality:** No explicit linter; follow Spring Boot conventions.  
**Database:** Flyway migrations run automatically.

### Frontend (React/TypeScript/Vite)

**Install:** `npm install`  
**Dev server:** `npm run dev` (port 5173)  
**Build:** `npm run build` (tsc + vite)  
**Lint:** `npm run lint` (ESLint)  
**Preview:** `npm run preview`  
**Testing:** No test framework configured.

### Market Data Service (Python/FastAPI)

**Install:** `pip install -r requirements.txt`  
**Run locally:** `uvicorn app.main:app --reload --port 8001`  
**Build Lambda:** `make build-marketdata` (creates function.zip)  
**Testing:** No test framework configured.

### Infrastructure (Terraform)

**Init:** `terraform init`  
**Plan:** `terraform plan`  
**Apply:** `terraform apply`  
**Destroy:** `terraform destroy`

### Root Makefile Commands

- `make dev` – Start dev environment (Docker Compose)
- `make dev-d` – Start in background
- `make dev-down` – Stop dev environment
- `make logs` – View logs
- `make build-marketdata` – Build Lambda package
- `make deploy-backend` – Build/push backend to ECR
- `make deploy-frontend` – Build/sync frontend to S3

## Code Style Guidelines

### Java (Backend)

**Imports:** Group: `java.*`, `jakarta.*`, `org.springframework.*`, `com.*`. No wildcards except static imports.  
**Naming:** Classes `PascalCase`, methods/variables `camelCase`, constants `UPPER_SNAKE_CASE`.  
**Error handling:** Use `GlobalExceptionHandler` (`@RestControllerAdvice`); return `ApiResponse<T>`. Log with SLF4J.  
**Entities/DTOs:** JPA annotations; Lombok `@Data` or explicit getters/setters. Use `BigDecimal` for monetary values with scale/rounding.  
**Testing:** JUnit 5 (`@Test`), `Given-When-Then`, mock with `@MockBean`.

### TypeScript/React (Frontend)

**Imports:** React first, third‑party libraries, then internal modules. Relative imports.  
**Naming:** Components `PascalCase`, functions/variables `camelCase`, constants `UPPER_SNAKE_CASE`.  
**Error handling:** Try/catch for async; display user‑friendly errors.  
**Styling:** CSS modules or plain CSS; CSS variables for theming.  
**State:** Local with `useState`; React Context if needed.  
**TypeScript:** Explicit types; interfaces for props; avoid `any`.

### Python (Market Data Service)

**Imports:** Stdlib → third‑party → local, blank‑line separated.  
**Naming:** Functions/variables `snake_case`, classes `PascalCase`, constants `UPPER_SNAKE_CASE`.  
**Error handling:** Raise `HTTPException`; use unified `create_response`. Log with `logging`.  
**API:** FastAPI + Pydantic; async endpoints; include `/health`.

### Terraform (Infrastructure)

**File organization:** `main.tf`, `variables.tf`, `providers.tf`, `modules/`.  
**Naming:** Resources/variables/outputs `snake_case`.  
**Best practices:** Use variables, add descriptions, keep secrets in `terraform.tfvars` (not committed).

## Development Workflow

1. `make dev` – start all services with hot reload.
2. Backend: Spring DevTools auto‑restart.
3. Frontend: Vite hot reload.
4. Market data: Uvicorn reload on file change.
5. DB changes: Add Flyway migrations in `src/main/resources/db/migration`.

## Commit & Pull Request Conventions

- **Commits:** Imperative mood ("Add feature", "Fix bug").
- **PR titles:** Summarize change, mention component.
- **PR description:** Context, testing steps, screenshots for UI.

## Notes for AI Assistants

- Maintain existing import grouping and Lombok usage in Java.
- Keep CSS in separate `.css` files for React components.
- Follow `create_response` pattern in Python.
- Run `npm run lint` for frontend changes; fix ESLint errors.
- Run `./mvnw test` for backend changes.
- Never commit secrets (`.env`, `terraform.tfvars`, `*.tfstate`).

## Missing Configurations

- No `.cursorrules` or `.github/copilot-instructions.md`.
- No frontend testing framework.
- No Java code formatter (Checkstyle/Spotless).
- No Python linter (flake8/black).  
  *Agents should propose adding these when improving code quality.*

---
*Last updated: 2025‑02‑02*