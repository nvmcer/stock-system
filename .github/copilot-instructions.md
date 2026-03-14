# Copilot Instructions for Stock System

## Project Overview
A cloud-native stock portfolio management system with full-stack monorepo architecture.

## Technology Stack
- **Backend:** Spring Boot 3.5 + Java 21 + PostgreSQL + Flyway
- **Frontend:** React 19 + TypeScript 5.9 + Vite 7
- **Infrastructure:** Terraform (Cloudflare Pages + Hetzner VPS + Neon PostgreSQL — in transition)
- **Observability:** Grafana + Loki + Prometheus + Alloy

## Critical Rules

### API Response Format
All REST endpoints MUST return the `ApiResponse<T>` envelope:
```json
{ "success": boolean, "code": string, "message": string, "data": T, "timestamp": string }
```

### Backend (Java)
- Use constructor injection (never `@Autowired` on fields).
- Use `BigDecimal` for all monetary values.
- DTO naming: `*RequestDto` for input, `*ResponseDto` for output.
- All controllers return `ApiResponse<T>`.
- All public methods must have Javadoc.
- Use SLF4J for logging.
- Flyway migrations: sequential version numbers, never modify existing files.

### Frontend (TypeScript/React)
- No `any` type – define explicit interfaces.
- API response types must mirror backend DTOs.
- One component per file, PascalCase filename matching component name.
- CSS in separate `.css` files (no inline styles, no CSS-in-JS).
- Handle loading/error states for all data-fetching pages.
- Use `interface` for object shapes, `type` for unions.

### Terraform
- Use `snake_case` for all resource names.
- Never hardcode credentials.
- Use `sensitive = true` for secret variables.

## Key Files to Reference
- `AGENTS.md` – Complete development guidelines and AI constraints.
- `STYLE_GUIDE.md` – API standards, error codes, code conventions.
- `stock-system-backend/src/main/java/com/exception/ApiResponse.java` – API envelope implementation.
- `stock-system-backend/src/main/java/com/exception/GlobalExceptionHandler.java` – Exception handling.
- `stock-system-frontend/src/services/api.ts` – Frontend API client.

## Language
- All code comments, Javadoc, commit messages, and documentation must be in English.
