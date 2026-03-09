# STYLE_GUIDE.md – Unified API Standards for Stock System

This document defines the unified API response format, error handling, and consistency standards for the stock system.

## Architecture

### Services
- **Backend (stock-system-backend):** Spring Boot Java application
- **Frontend (stock-system-frontend):** React + TypeScript + Vite
- **Infrastructure (stock-system-infra):** Terraform

### Database
- PostgreSQL (local or Hetzner VPS)

### External APIs
- Finnhub API for stock market data (integrated directly into Spring Boot)

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
| `message` | string | Yes | Human‑readable description of the outcome. For errors, this should be user‑friendly. |
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

### Partial Success / Multi‑Status
When an operation processes multiple items and some succeed while others fail, use `code: "206"` (Partial Content) and include per‑item status in `data`.

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

## Implementation Guidelines

### Java (Spring Boot)
1. **Use `ApiResponse<T>` class** with fields: `success`, `code`, `message`, `data`, `timestamp`.
2. **Use `@RestControllerAdvice`** to wrap all exceptions in the new envelope.
3. **Controller methods** return `ApiResponse<T>`.
4. **Static helpers:** `ApiResponse.success(data, message)` and `ApiResponse.error(code, message)`.
5. **Timestamp generation** uses ISO 8601 UTC (`Instant.now().toString()`).

Example controller method:
```java
@GetMapping("/{id}")
public ApiResponse<StockResponseDto> getStock(@PathVariable Long id) {
    StockResponseDto stock = stockService.findById(id);
    return ApiResponse.success(stock, "Stock retrieved");
}
```

### Frontend (React/TypeScript)
1. **API client** expects the envelope for **all** responses.
2. **Access payload** as `response.data.data` (first `data` is Axios response, second is envelope field).
3. **Check `success` flag** before processing; handle errors uniformly.

Example adapted service:
```typescript
const response = await api.get("/api/stocks");
if (response.data.success) {
  setStocks(response.data.data); // envelope's data field
} else {
  alert(`Error ${response.data.code}: ${response.data.message}`);
}
```

## Additional API Conventions

### URL Design
- Use plural nouns for resources (`/api/stocks`, `/api/users`).
- Use HTTP methods appropriately: GET (read), POST (create), PUT/PATCH (update), DELETE (remove).
- Nest related resources: `/api/users/{userId}/portfolio`.
- Use query parameters for filtering, sorting, pagination.

### Request/Response Naming
- Request DTOs: suffix `RequestDto` (e.g., `StockRequestDto`).
- Response DTOs: suffix `ResponseDto` (e.g., `StockResponseDto`).
- Use `@JsonProperty` to ensure snake_case in JSON.

### Validation
- Use Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email`) in Java.
- Return `code: "400"` with validation errors in `data` field.

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
*Last updated: 2026-03-07*  
*Owners: Backend Team, Frontend Team*
