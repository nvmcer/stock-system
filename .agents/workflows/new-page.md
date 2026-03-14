---
description: How to create a new frontend page
---

# New Page Workflow

This workflow guides the creation of a new React page component.

## Prerequisites
- Read `STYLE_GUIDE.md` for frontend code standards.
- Check `AGENTS.md` for cross-module dependency mapping.

## Steps

### 1. Create the Page Component
- Location: `stock-system-frontend/src/pages/{Name}Page.tsx`
- Name must be PascalCase with `Page` suffix.
- Define TypeScript interfaces for all data structures.

### 2. Create the CSS File
- Location: `stock-system-frontend/src/pages/{Name}Page.css` (or place in `components/` if shared)
- Use CSS variables defined in `index.css` for theming consistency.
- Follow BEM-like naming: `.page-name__element--modifier`.

### 3. Add Required State
- Loading state: `const [loading, setLoading] = useState(true);`
- Error state: `const [error, setError] = useState<string | null>(null);`
- Data state: `const [data, setData] = useState<DataType[]>([]);`

### 4. Implement API Integration
- Import API client from `services/api.ts`.
- Use `useEffect` for initial data fetch.
- Handle `ApiResponse` envelope:
  ```typescript
  if (response.data.success) {
    setData(response.data.data);
  } else {
    setError(`Error ${response.data.code}: ${response.data.message}`);
  }
  ```

### 5. Add Route
- Update `App.tsx` to add the new route.
- Update `Layout.tsx` navigation if applicable.

### 6. Handle Edge Cases
- Empty state (no data).
- Loading spinner/skeleton.
- Error display with retry option.
- Auth redirect if endpoint requires authentication.

// turbo
### 7. Lint Check
```bash
cd stock-system-frontend && npm run lint
```

### 8. Visual Verification
- Start dev server: `make dev`
- Navigate to the new page in browser at `http://localhost:3001`.
- Verify responsive layout, loading states, error handling.
