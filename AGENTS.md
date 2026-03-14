# AGENTS.md – Stock System Development Guidelines

Build commands, code style, and conventions for agentic coding assistants.

## Project Structure

- `stock-system-backend/` – Spring Boot 3.5 + Java 21 backend (includes Finnhub market data integration)
- `stock-system-frontend/` – React 19 + TypeScript 5.9 + Vite 7 frontend
- `stock-system-infra/` – Terraform infrastructure as code (architecture in transition)
- `logging-config/` – Observability configs (Grafana, Loki, Prometheus, Alloy)
- Root `docker-compose.dev.yml` – Local development environment
- Root `Makefile` – Common automation commands

## AI Agent Roles

### Architect
- Role: Senior System Architect & AI Coordination Lead
- Capability: Project analysis, standard setting, cross-module impact analysis, consistency checking.
- Instructions:
  1. Always read `STYLE_GUIDE.md` before suggesting code changes.
  2. When modifying shared interfaces (API contracts, DTOs), verify both frontend and backend consistency.
  3. Consult `stock-system-backend/src/main/resources/db/migration/` before proposing schema changes.
  4. Validate all changes against the unified `ApiResponse<T>` envelope format.

### Refactor_Bot
- Role: Code Refactoring Specialist
- Capability: Applying patterns, adding Javadoc/type hints, fixing anti-patterns.
- Instructions:
  1. Preserve existing import grouping and Lombok usage in Java.
  2. Keep CSS in separate `.css` files for React components.
  3. Never introduce `any` types in TypeScript.

### Code_Reviewer
- Role: Automated Code Review Agent
- Capability: Pre-commit validation, style enforcement, dependency audit.
- Instructions:
  1. Run `npm run lint` for all frontend changes.
  2. Run `./mvnw test -f stock-system-backend/pom.xml` for all backend changes.
  3. Verify new API endpoints conform to `ApiResponse<T>` envelope.

## AI Agent Behavioral Constraints

### Mandatory Pre-Checks
Before making any code change, AI agents MUST:
1. Read `STYLE_GUIDE.md` for API standards.
2. Check existing code patterns in the target module.
3. Verify Flyway migration sequence – current latest: `V7__add_realizedPnl_to_portfolio.sql`.
4. Run `npm run lint` for frontend changes.
5. Run `./mvnw test -f stock-system-backend/pom.xml` for backend changes.

### Prohibited Actions
- Never modify the `ApiResponse<T>` envelope structure without explicit approval.
- Never add new dependencies without documenting rationale in the commit message.
- Never create API endpoints outside the `/api/` prefix.
- Never store secrets in code files – use environment variables.
- Never create Flyway migrations with gaps in version numbers.
- Never use `any` type in TypeScript.
- Never commit `.env`, `terraform.tfvars`, or `*.tfstate` files.
- Never modify existing Flyway migration files – always create new ones.

### Code Generation Requirements
- All new Java classes must include an SLF4J logger: `private static final Logger log = LoggerFactory.getLogger(ClassName.class);`
- All new REST endpoints must return `ApiResponse<T>`.
- All new React components must have a corresponding `.css` file.
- All DTO classes must follow `*RequestDto` / `*ResponseDto` naming.
- All new entities must have a corresponding Flyway migration.
- All controller methods must be annotated with `@Operation` (SpringDoc OpenAPI).

## Cross-Module Dependency Map

### API Contract Flow
```
Frontend (services/api.ts) → Backend Controllers → Services → Repositories → PostgreSQL
```

### Key Shared Contracts
| Contract | Frontend Location | Backend Location |
|----------|------------------|------------------|
| API Response Envelope | `services/api.ts` | `exception/ApiResponse.java` |
| Stock DTO | `pages/StocksPage.tsx` | `stock/dto/StockResponseDto.java` |
| Trade DTO | `pages/TradesPage.tsx` | `trades/dto/TradeResponseDto.java` |
| Portfolio DTO | `pages/PortfolioPage.tsx` | `portfolio/dto/PortfolioResponseDto.java` |
| Auth (JWT) | `services/api.ts` interceptor | `security/JwtUtil.java` |
| User DTO | `pages/LoginPage.tsx` | `user/dto/UserResponseDto.java` |

### Change Impact Matrix
| When modifying... | Also check/update... |
|-------------------|---------------------|
| Stock entity | `StockResponseDto`, `StockRequestDto`, `StockController`, `StocksPage.tsx`, `api.ts` |
| Trade entity | `TradeResponseDto`, `TradeRequestDto`, `TradeController`, `TradesPage.tsx`, `api.ts` |
| Auth flow | `SecurityConfig`, `JwtUtil`, `JwtAuthenticationFilter`, `LoginPage.tsx`, `api.ts` |
| API response format | `ApiResponse.java`, `GlobalExceptionHandler.java`, `api.ts`, ALL `*Page.tsx` files |
| Database schema | Add new Flyway migration, update entity, update DTOs |

## Build, Lint, and Test Commands

### Backend (Java/Spring Boot)

**Build:** `./mvnw clean package` (or `-DskipTests`)
**Run tests:** `./mvnw test`
**Single test:** `./mvnw test -Dtest=ClassName`
**Run locally:** `./mvnw spring-boot:run`
**Code quality:** Follow Spring Boot conventions; Lombok for boilerplate reduction.
**Database:** Flyway migrations run automatically on startup.
**Environment:** Set `FINNHUB_API_KEY` for market data in `.env`.

### Frontend (React/TypeScript/Vite)

**Install:** `npm install`
**Dev server:** `npm run dev` (port 5173, mapped to host 3001 in Docker)
**Build:** `npm run build` (tsc + vite)
**Lint:** `npm run lint` (ESLint)
**Preview:** `npm run preview`

### Infrastructure (Terraform)

**Init:** `terraform init`
**Plan:** `terraform plan`
**Apply:** `terraform apply`
**Destroy:** `terraform destroy`
**State:** Managed via remote backend (never commit `.tfstate` files).

### Root Makefile Commands

- `make dev` – Start dev environment (Docker Compose with hot reload)
- `make dev-d` – Start in background
- `make dev-down` – Stop dev environment
- `make logs` – View logs

## Code Style Guidelines

### Java (Backend)

**Imports:** Group: `java.*`, `jakarta.*`, `org.springframework.*`, `com.*`. No wildcards except static imports.
**Naming:** Classes `PascalCase`, methods/variables `camelCase`, constants `UPPER_SNAKE_CASE`.
**Error handling:** Use `GlobalExceptionHandler` (`@RestControllerAdvice`); return `ApiResponse<T>`. Log with SLF4J.
**Entities/DTOs:** JPA annotations; Lombok `@Data` or explicit getters/setters. Use `BigDecimal` for monetary values with scale/rounding.
**Testing:** JUnit 5 (`@Test`), `Given-When-Then`, mock with `@MockBean`.
**Javadoc:** All public classes and methods must have Javadoc comments.

### TypeScript/React (Frontend)

**Imports:** React first, third-party libraries, then internal modules. Relative imports.
**Naming:** Components `PascalCase`, functions/variables `camelCase`, constants `UPPER_SNAKE_CASE`.
**Error handling:** Try/catch for async; display user-friendly errors via `ApiResponse` envelope.
**Styling:** CSS modules or plain CSS files (one per component); CSS variables for theming.
**State:** Local with `useState`; React Context for auth/global state.
**TypeScript:** Explicit types; interfaces for props and API responses; avoid `any`.
**Components:** One component per file; filename matches component name.

### Terraform (Infrastructure)

**File organization:** `main.tf`, `variables.tf`, `providers.tf`, `modules/`.
**Naming:** Resources/variables/outputs `snake_case`.
**Best practices:** Use variables with descriptions, keep secrets in `terraform.tfvars` (not committed), use remote state backend.

## Database Migration Rules

- Current latest migration: `V7__add_realizedPnl_to_portfolio.sql`
- Next migration must be: `V8__<description>.sql`
- Always use descriptive `snake_case` filenames
- Never modify existing migration files – create new ones
- Test migrations with `./mvnw flyway:validate`
- Include comments explaining the purpose of the migration
- Location: `stock-system-backend/src/main/resources/db/migration/`

## File Templates for AI Code Generation

### New Java Controller
```
Location: src/main/java/com/{domain}/controller/{Name}Controller.java
Required: @RestController, @RequestMapping("/api/{domain}"), SLF4J Logger, ApiResponse<T> returns
Optional: @Operation annotations for OpenAPI documentation
```

### New Java Service
```
Location: src/main/java/com/{domain}/service/{Name}Service.java
Required: @Service, SLF4J Logger, constructor injection (no @Autowired on fields)
```

### New React Page
```
Location: src/pages/{Name}Page.tsx
Required: Typed props/state, error handling with ApiResponse envelope, corresponding CSS file
```

### New React Component
```
Location: src/components/{Name}.tsx + src/components/{Name}.css
Required: Typed props interface, CSS module or plain CSS file alongside
```

### New Flyway Migration
```
Location: src/main/resources/db/migration/V{N}__{description}.sql
Required: Comment header with purpose and date, idempotent where possible
```

## Development Workflow

1. `make dev` – Start all services with hot reload.
2. Backend: Spring DevTools auto-restart on code changes.
3. Frontend: Vite hot reload on code changes.
4. DB changes: Add Flyway migrations in `src/main/resources/db/migration/`.
5. Debug: Attach VSCode debugger (backend port 8787, frontend via Chrome DevTools).

## Commit & Pull Request Conventions

- **Commits:** Imperative mood ("Add feature", "Fix bug"), prefix with component: `[backend]`, `[frontend]`, `[infra]`.
- **PR titles:** Summarize change, mention component.
- **PR description:** Context, testing steps, screenshots for UI changes.

## Observability Stack

- **Grafana** (port 3000): Dashboards for API latency, error rates, business metrics.
- **Loki** (port 3100): Centralized log aggregation (JSON format via Logstash encoder).
- **Prometheus** (port 9090): Metrics collection from Spring Boot Actuator + Micrometer.
- **Alloy** (port 12345): Log collection agent forwarding Docker container logs to Loki.

## Notes for AI Assistants

- Maintain existing import grouping and Lombok usage in Java.
- Keep CSS in separate `.css` files for React components.
- All API responses must use unified `ApiResponse<T>` envelope (see `STYLE_GUIDE.md`).
- Run `npm run lint` for frontend changes; fix ESLint errors before committing.
- Run `./mvnw test` for backend changes; fix test failures before committing.
- Never commit secrets (`.env`, `terraform.tfvars`, `*.tfstate`).
- When unsure about a pattern, check existing code in the same module first.
- Use English for all code comments, Javadoc, commit messages, and documentation.

---
*Last updated: 2026-03-14*
