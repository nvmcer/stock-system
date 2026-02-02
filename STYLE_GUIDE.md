# STYLE_GUIDE.md – Unified API Standards for Stock System

This document defines the unified API response format, error handling, and consistency standards for all services in the stock system. It is based on analysis of current inconsistencies between the Java Spring Boot backend and Python FastAPI market data service.

## Current State Analysis

### Java Backend (Spring Boot)
- **Mixed response patterns:**
  - Raw DTOs/Entities (e.g., `List<StockResponseDto>`, `Trade`, `User`)
  - `ResponseEntity<?>` with `Map<String, Object>` (e.g., `/api/auth/login`)
  - `ResponseEntity<List<TradeResponseDto>>`
  - `void` for delete operations
- **Error handling:** Uses `GlobalExceptionHandler` returning `ApiResponse<T>` with fields:
  ```json
  { "status": 401, "message": "Unauthorized: ...", "data": null }
  ```
- **No unified success wrapper:** Successful responses do not use `ApiResponse`.

### Python FastAPI (Market Data Service)
- **Consistent wrapper pattern:** All endpoints return:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "Operation successful",
    "data": { ... },
    "timestamp": "2025-02-02T10:30:00Z"
  }
  ```
- **Error handling:** Same envelope with `success: false`, `code` as string, `message` describing error.

### Critical Inconsistencies
1. **Success responses:** Java returns raw data, FastAPI wraps everything.
2. **Error structure:** Java uses `status` (int) + `message`, FastAPI uses `success` + `code` (string) + `message`.
3. **Metadata:** FastAPI includes `timestamp`; Java does not.
4. **Client‑side expectations:** Frontend expects raw `data` for successful calls (e.g., `res.data` = array of stocks) but expects `err.response.data.message` for errors (wrapped).
5. **Backend‑to‑backend call:** `MarketDataClientImpl` expects `Map<String, Double>` as the root response, but FastAPI returns the wrapper – this will cause deserialization failures.

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
1. **Create a unified `ApiResponse` class** (replace existing `com.exception.ApiResponse`) with fields: `success`, `code`, `message`, `data`, `timestamp`.
2. **Use `@RestControllerAdvice`** to wrap all exceptions in the new envelope.
3. **Modify all controller methods** to return `ResponseEntity<ApiResponse<T>>` (or `ApiResponse<T>` with `@ResponseBody`).
4. **Provide a static helper** `ApiResponse.success(data, message)` and `ApiResponse.error(code, message)`.
5. **Ensure timestamp generation** uses ISO 8601 UTC (e.g., `Instant.now().toString()`).

Example controller method:
```java
@GetMapping("/{id}")
public ApiResponse<StockResponseDto> getStock(@PathVariable Long id) {
    StockResponseDto stock = stockService.findById(id);
    return ApiResponse.success(stock, "Stock retrieved");
}
```

### Python (FastAPI)
1. **Keep existing `create_response` helper** – it already conforms to the standard.
2. **Ensure all endpoints** use `create_response` (already satisfied).
3. **Standardize `code` values** as strings (already done).
4. **Use HTTP status codes** that match the numeric `code` (e.g., `status_code=200` when `code="200"`).

### Frontend (React/TypeScript)
1. **Update API client** to expect the envelope for **all** responses.
2. **Access payload** as `response.data.data` (first `data` is Axios response, second is envelope field).
3. **Check `success` flag** before processing; handle errors uniformly.
4. **Phase‑in strategy:** Initially, adapt client to handle both wrapped and raw responses during migration.

Example adapted service:
```typescript
const response = await api.get("/api/stocks");
if (response.data.success) {
  setStocks(response.data.data); // envelope's data field
} else {
  alert(`Error ${response.data.code}: ${response.data.message}`);
}
```

### Backend‑to‑Backend Calls
1. **Update `MarketDataClientImpl`** to expect the envelope and extract `data`.
2. **Or modify FastAPI endpoint** to return raw `Map<String, Double>` (not recommended; breaks consistency).
3. **Preferred approach:** Keep envelope and update client to unwrap `data`.

## Migration Plan

### Phase 1: Define Standard & Update Shared Components
- Create/update `ApiResponse` class in Java.
- Ensure FastAPI `create_response` matches standard exactly.
- Update `GlobalExceptionHandler` to use new envelope.

### Phase 2: Update Critical Endpoints
- Update `/api/auth/login` and `/api/auth/register` to use envelope.
- Update `/api/stocks`, `/api/trades`, `/api/portfolio` endpoints.
- Update frontend to handle wrapped responses for these endpoints.

### Phase 3: Update Remaining Endpoints
- Gradually convert all other controllers.
- Update frontend as needed.

### Phase 4: Remove Legacy Support
- Once all endpoints are migrated, remove compatibility code.
- Enforce envelope usage via code reviews / linting.

## Additional API Conventions

### URL Design
- Use plural nouns for resources (`/api/stocks`, `/api/users`).
- Use HTTP methods appropriately: GET (read), POST (create), PUT/PATCH (update), DELETE (remove).
- Nest related resources: `/api/users/{userId}/portfolio`.
- Use query parameters for filtering, sorting, pagination.

### Request/Response Naming
- Request DTOs: suffix `RequestDto` (e.g., `StockRequestDto`).
- Response DTOs: suffix `ResponseDto` (e.g., `StockResponseDto`).
- Use `@JsonProperty` (Java) or `alias` (Pydantic) to ensure snake_case in JSON.

### Validation
- Use Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email`) in Java.
- Use Pydantic validators in Python.
- Return `code: "400"` with validation errors in `data` field.

### Documentation
- Annotate Java controllers with `@Operation` (SpringDoc OpenAPI).
- Annotate FastAPI endpoints with `@app.get(..., response_model=...)`.
- Keep OpenAPI spec synchronized.

## Monitoring & Compliance
- Add automated tests that verify envelope structure for all endpoints.
- Use integration tests to ensure Java/FastAPI interoperability.
- Include envelope checks in CI pipeline.

## Exceptions
- **Health checks** (`/health`, `/actuator/health`) may return a simplified format (compatible with monitoring tools).
- **Static assets** (frontend files) are not subject to this standard.

---
*Last updated: 2025‑02‑02*  
*Owners: Backend Team, Frontend Team, DevOps Team*