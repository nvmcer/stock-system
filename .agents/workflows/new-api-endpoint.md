---
description: How to create a new REST API endpoint (backend + frontend)
---

# New API Endpoint Workflow

This workflow guides the creation of a new REST API endpoint from backend to frontend.

## Prerequisites
- Read `STYLE_GUIDE.md` for the `ApiResponse<T>` envelope standard.
- Read `AGENTS.md` for coding conventions and cross-module mapping.

## Steps

### 1. Identify the Domain
Determine which domain module the endpoint belongs to: `stock`, `trades`, `portfolio`, `user`, or create a new one.

### 2. Create/Update Entity (if needed)
- Location: `stock-system-backend/src/main/java/com/{domain}/entity/{Name}.java`
- Add JPA annotations, Lombok `@Data`.
- Use `BigDecimal` for monetary values.

### 3. Create Flyway Migration (if schema change)
- Check current latest migration version in `stock-system-backend/src/main/resources/db/migration/`.
- Create next sequential migration: `V{N}__{description}.sql`.
- Add comment header explaining purpose.

### 4. Create/Update DTOs
- Request: `stock-system-backend/src/main/java/com/{domain}/dto/{Name}RequestDto.java`
- Response: `stock-system-backend/src/main/java/com/{domain}/dto/{Name}ResponseDto.java`
- Add Jakarta validation annotations to request DTOs.
- Add `@JsonProperty` for snake_case JSON field names.

### 5. Create/Update Repository
- Location: `stock-system-backend/src/main/java/com/{domain}/repository/{Name}Repository.java`
- Extend `JpaRepository<Entity, Long>`.

### 6. Create/Update Service
- Location: `stock-system-backend/src/main/java/com/{domain}/service/{Name}Service.java`
- Annotate with `@Service`.
- Add SLF4J logger.
- Use constructor injection.

### 7. Create/Update Controller
- Location: `stock-system-backend/src/main/java/com/{domain}/controller/{Name}Controller.java`
- Annotate with `@RestController`, `@RequestMapping("/api/{domain}")`.
- Add `@Operation` for OpenAPI docs.
- Return `ApiResponse<T>` for all methods.

### 8. Write Backend Tests
- Location: `stock-system-backend/src/test/java/com/{domain}/`
- Use JUnit 5, `Given-When-Then` pattern.
- Mock dependencies with `@MockBean`.

// turbo
### 9. Run Backend Tests
```bash
cd stock-system-backend && ./mvnw test
```

### 10. Update Frontend Types
- Add TypeScript interfaces mirroring the response DTO in the page or a shared types file.

### 11. Add Frontend API Call
- Add the API call in `stock-system-frontend/src/services/api.ts` or in the page component.
- Handle `ApiResponse` envelope: check `success` flag, extract `data`.

### 12. Create/Update Frontend Page
- Location: `stock-system-frontend/src/pages/{Name}Page.tsx`
- Include loading state, error state, and data rendering.
- Create corresponding CSS file.

// turbo
### 13. Run Frontend Lint
```bash
cd stock-system-frontend && npm run lint
```
