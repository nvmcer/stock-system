# STYLE_GUIDE.md – Unified Standards for Stock System

This document defines the unified API response format, frontend code standards, error handling, observability, and consistency standards for the stock system.

## Architecture

### Services
- **Backend (stock-system-backend):** Spring Boot 3.5 + Java 21
- **Frontend (stock-system-frontend):** React 19 + TypeScript 5.9 + Vite 7
- **Infrastructure (stock-system-infra):** Terraform (architecture in transition)
- **Observability:** Grafana Alloy, Loki, Prometheus, Grafana (Docker Compose)

### Database
- PostgreSQL via Neon (serverless, production) or local Docker (development)
- Migrations: Flyway (auto-run on startup)

### External APIs
- Finnhub API for real-time stock market data (integrated into Spring Boot via `FinnhubClient`)

---

## Unified API Response Standard

All HTTP API endpoints **must** adhere to the following envelope format.

### Envelope Structure
```json
{
  "success": boolean,
  "code": string,
  "message": string,
  "data": any,
  "timestamp": string
}
```

### Field Definitions
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `success` | boolean | Yes | `true` for successful operations, `false` for errors. |
| `code` | string | Yes | HTTP status code as a string (e.g., `"200"`, `"400"`, `"500"`). |
| `message` | string | Yes | Human-readable description of the outcome. For errors, this should be user-friendly. |
| `data` | any | No | Payload of the response. Must be `null` for errors unless extra error details are needed. |
| `timestamp` | string | Yes | ISO 8601 UTC timestamp (e.g., `"2025-02-02T10:30:00Z"`). |

### HTTP Status Codes
- The HTTP status code must match the numeric value of `code` (e.g., `code: "200"` ↔ HTTP 200 OK).
- Use standard HTTP status codes (2xx for success, 4xx for client errors, 5xx for server errors).

### Success Response Example
```json
{
  "success": true,
  "code": "200",
  "message": "Stock prices retrieved successfully",
  "data": {
    "AAPL": 182.63,
    "TSLA": 195.71
  },
  "timestamp": "2025-02-02T10:30:00Z"
}
```

### Error Response Example
```json
{
  "success": false,
  "code": "404",
  "message": "User with ID 123 not found",
  "data": null,
  "timestamp": "2025-02-02T10:30:00Z"
}
```

### Partial Success / Multi-Status
When an operation processes multiple items and some succeed while others fail, use `code: "206"` (Partial Content) and include per-item status in `data`.

Example (batch price fetch):
```json
{
  "success": true,
  "code": "206",
  "message": "Partial success with errors",
  "data": {
    "AAPL": 182.63,
    "TSLA": null
  },
  "timestamp": "2025-02-02T10:30:00Z"
}
```

---

## Error Code Registry

All API endpoints must use the following standardized error codes and message patterns.

| Code | Context | Message Template | HTTP Status |
|------|---------|------------------|-------------|
| `200` | Success | Descriptive success message | 200 OK |
| `201` | Created | `"{Resource} created successfully"` | 201 Created |
| `206` | Partial | `"Partial success with errors"` | 206 Partial Content |
| `400` | Validation | `"Invalid input: {field} {reason}"` | 400 Bad Request |
| `401` | Auth | `"Authentication required"` or `"Invalid credentials"` | 401 Unauthorized |
| `403` | Auth | `"Insufficient permissions"` | 403 Forbidden |
| `404` | Resource | `"{Resource} with ID {id} not found"` | 404 Not Found |
| `409` | Conflict | `"{Resource} already exists"` | 409 Conflict |
| `422` | Business | `"Cannot {action}: {reason}"` | 422 Unprocessable |
| `429` | Rate Limit | `"Too many requests. Please try again later."` | 429 Too Many Requests |
| `500` | Server | `"Internal server error. Please try again later."` | 500 Internal Server Error |
| `503` | Unavailable | `"Service temporarily unavailable"` | 503 Service Unavailable |

### Validation Error Response
When validation fails, include field-level errors in the `data` field:
```json
{
  "success": false,
  "code": "400",
  "message": "Validation failed",
  "data": {
    "errors": [
      { "field": "email", "message": "Must be a valid email address" },
      { "field": "symbol", "message": "Must not be blank" }
    ]
  },
  "timestamp": "2025-02-02T10:30:00Z"
}
```

---

## Backend Implementation Guidelines (Java / Spring Boot)

### API Response
1. **Use `ApiResponse<T>` class** with fields: `success`, `code`, `message`, `data`, `timestamp`.
2. **Use `@RestControllerAdvice`** (`GlobalExceptionHandler`) to wrap all exceptions in the envelope.
3. **Controller methods** return `ApiResponse<T>`.
4. **Static helpers:** `ApiResponse.success(data, message)` and `ApiResponse.error(code, message)`.
5. **Timestamp generation** uses ISO 8601 UTC (`Instant.now().toString()`).

Example controller method:
```java
@GetMapping("/{id}")
@Operation(summary = "Get stock by ID")
public ApiResponse<StockResponseDto> getStock(@PathVariable Long id) {
    StockResponseDto stock = stockService.findById(id);
    return ApiResponse.success(stock, "Stock retrieved successfully");
}
```

### Package Structure
```
com/
├── config/              # Spring configurations (Security, CORS, etc.)
├── exception/           # ApiResponse, GlobalExceptionHandler
├── external/            # External API clients
│   └── finnhub/         # Finnhub market data client
├── security/            # JWT, UserDetailsService, filters
├── stock/               # Stock domain (controller, dto, entity, repository, service)
├── trades/              # Trade domain
├── portfolio/           # Portfolio domain
├── user/                # User domain
└── stockManagePortfolio/ # Application entry point
```

### Coding Standards
- **Dependency Injection:** Constructor injection (never `@Autowired` on fields).
- **Logging:** SLF4J via `LoggerFactory.getLogger(ClassName.class)`.
- **Monetary Values:** Always use `BigDecimal` with explicit scale and `RoundingMode.HALF_UP`.
- **DTOs:** Suffix `RequestDto` for input, `ResponseDto` for output. Use `@JsonProperty` for snake_case JSON.
- **Validation:** Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email`, `@DecimalMin`).
- **Documentation:** Annotate controllers with `@Operation` (SpringDoc OpenAPI).

---

## Frontend Implementation Guidelines (React / TypeScript)

### API Client Pattern
1. **API client** (`services/api.ts`) expects the envelope for **all** responses.
2. **Access payload** as `response.data.data` (first `data` is Axios response, second is envelope field).
3. **Check `success` flag** before processing; handle errors uniformly.

Example:
```typescript
interface StockResponse {
  id: number;
  symbol: string;
  companyName: string;
  currentPrice: number;
}

const fetchStocks = async (): Promise<StockResponse[]> => {
  const response = await api.get("/api/stocks");
  if (response.data.success) {
    return response.data.data;
  } else {
    throw new Error(`Error ${response.data.code}: ${response.data.message}`);
  }
};
```

### Component Structure
- **One component per file.** Filename must match component name (`PascalCase`).
- **CSS file alongside component:** `ComponentName.tsx` + `ComponentName.css`
- **Props interface** defined at the top of the file or in a shared types file.

Example component structure:
```
src/
├── components/
│   ├── Layout.tsx
│   └── Layout.css
├── pages/
│   ├── StocksPage.tsx
│   ├── TradesPage.tsx
│   └── LoginPage.tsx
├── services/
│   └── api.ts
├── App.tsx
├── App.css
├── index.css
└── main.tsx
```

### State Management
- **Local state:** `useState` for component-specific data.
- **Auth/global state:** React Context.
- **API data:** Fetched in components via `useEffect` + service layer.
- **Loading/error states:** Every page that fetches data must handle loading and error states.

### Type Safety Requirements
- **No `any` type** – define explicit interfaces for all data structures.
- **API response types** must mirror backend DTOs.
- **Use `interface`** for object shapes, `type` for unions/intersections.
- **Props** must be typed via interface.

### Error Handling Pattern
```typescript
const [error, setError] = useState<string | null>(null);
const [loading, setLoading] = useState(true);

useEffect(() => {
  const loadData = async () => {
    try {
      setLoading(true);
      const response = await api.get("/api/resource");
      if (response.data.success) {
        setData(response.data.data);
      } else {
        setError(`Error ${response.data.code}: ${response.data.message}`);
      }
    } catch (err) {
      setError("Network error. Please try again.");
    } finally {
      setLoading(false);
    }
  };
  loadData();
}, []);
```

### CSS Conventions
- Use CSS variables for theming (colors, spacing, typography).
- Follow BEM-like naming where applicable: `.component__element--modifier`.
- Define global variables in `index.css`.
- Component-specific styles in `ComponentName.css`.

---

## Terraform Standards

> **Note:** The infrastructure is currently in transition. The Terraform modules below reflect the planned architecture (Cloudflare + Hetzner + Neon). Existing AWS modules are being deprecated.

### Module Structure (Planned)
```
stock-system-infra/
├── main.tf           # Root module, orchestrates child modules
├── variables.tf      # Input variables with descriptions
├── providers.tf      # Provider configurations
└── modules/
    ├── hetzner/      # Hetzner VPS provisioning
    ├── neon/         # Neon PostgreSQL database
    ├── cloudflare/   # Cloudflare Pages + DNS
    └── docker/       # Container deployment configs
```

### Naming Convention
- Resources: `stock_system_{resource_type}_{environment}`
- Variables: descriptive `snake_case` with `description` and `type`
- Outputs: `{module}_{resource_type}_{attribute}`

### Security Requirements
- No hardcoded credentials in `.tf` files.
- Use `sensitive = true` for secret variables.
- Database connections use SSL/TLS.
- Firewall rules follow least-privilege principle.
- Use remote state backend for state management.

---

## Observability Standards

### Logging Format
All backend services use JSON structured logging via Logstash Logback Encoder.

Required log fields:
- `timestamp` – ISO 8601 format
- `level` – Standard log level (INFO, WARN, ERROR, DEBUG)
- `logger` – Fully qualified class name
- `message` – Human-readable description
- `traceId` – Distributed trace identifier (via Micrometer Tracing)

Business event log fields (when applicable):
- `userId` – Acting user identifier
- `action` – Business action (e.g., `TRADE_EXECUTED`, `STOCK_PRICE_UPDATED`)
- `resourceType` – Affected resource type
- `resourceId` – Affected resource identifier

### Metrics (Micrometer + Prometheus)
- All API endpoints automatically expose latency and request count via Spring Actuator.
- Custom business metrics should be registered as Micrometer meters:
  - `trade_executed_total` – Counter for executed trades
  - `portfolio_value_total` – Gauge for aggregate portfolio value
  - `stock_price_update_total` – Counter for price update operations

### Health Checks
- Spring Actuator health endpoint: `/actuator/health`
- Includes database connectivity, disk space, and custom health indicators.

### Grafana Dashboards
- API Latency Dashboard (p50, p95, p99 response times)
- Error Rate Dashboard (4xx/5xx by endpoint)
- Business Metrics Dashboard (trades, portfolio values, price updates)

---

## Additional API Conventions

### URL Design
- Use plural nouns for resources (`/api/stocks`, `/api/users`).
- Use HTTP methods appropriately: GET (read), POST (create), PUT/PATCH (update), DELETE (remove).
- Nest related resources: `/api/users/{userId}/portfolio`.
- Use query parameters for filtering, sorting, pagination.

### Request/Response Naming
- Request DTOs: suffix `RequestDto` (e.g., `StockRequestDto`).
- Response DTOs: suffix `ResponseDto` (e.g., `StockResponseDto`).
- Use `@JsonProperty` to ensure `snake_case` in JSON output.

### Validation
- Use Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email`) in Java.
- Return `code: "400"` with validation errors in `data` field (see Error Code Registry).

### Documentation
- Annotate Java controllers with `@Operation` (SpringDoc OpenAPI).
- Keep OpenAPI spec synchronized.

## Monitoring & Compliance
- Add automated tests that verify envelope structure for all endpoints.
- Include envelope checks in CI pipeline.

## Exceptions
- **Health checks** (`/health`, `/actuator/health`) may return a simplified format (compatible with monitoring tools).
- **Static assets** (frontend files) are not subject to this standard.

---
*Last updated: 2026-03-14*
*Owners: Backend Team, Frontend Team*
