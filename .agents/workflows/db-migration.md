---
description: How to create and manage database migrations
---

# Database Migration Workflow

This workflow guides the creation and validation of Flyway database migrations.

## Prerequisites
- Read `AGENTS.md` for the current latest migration version.
- Read existing migrations in `stock-system-backend/src/main/resources/db/migration/` for patterns.

## Steps

### 1. Determine Next Version Number
- List existing migrations to find the latest version:
  ```bash
  ls stock-system-backend/src/main/resources/db/migration/
  ```
- Next version must be sequential (no gaps). If latest is `V7`, next is `V8`.

### 2. Create Migration File
- Location: `stock-system-backend/src/main/resources/db/migration/V{N}__{description}.sql`
- Use double underscore `__` between version number and description.
- Description should be descriptive `snake_case`.

### 3. Write SQL
- Include a comment header:
  ```sql
  -- V{N}: {Brief description}
  -- Purpose: {Detailed explanation}
  -- Date: {YYYY-MM-DD}
  ```
- Use idempotent statements where possible (`IF NOT EXISTS`, `IF EXISTS`).
- Consider rollback implications.

### 4. Update Entity Class
- Update the corresponding JPA entity in `stock-system-backend/src/main/java/com/{domain}/entity/`.
- Ensure entity fields match database columns.

### 5. Update DTOs (if needed)
- If the schema change affects API output, update response DTOs.
- If it affects API input, update request DTOs and validation annotations.

// turbo
### 6. Validate Migration
```bash
cd stock-system-backend && ./mvnw flyway:validate
```

// turbo
### 7. Run Tests
```bash
cd stock-system-backend && ./mvnw test
```

### 8. Verify in Dev Environment
- Start dev environment: `make dev`
- Check backend logs for successful migration.
- Verify data integrity via API or direct database query.

## Common Patterns

### Add Column
```sql
ALTER TABLE table_name ADD COLUMN column_name data_type constraints;
```

### Create Table
```sql
CREATE TABLE IF NOT EXISTS table_name (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Add Index
```sql
CREATE INDEX IF NOT EXISTS idx_table_column ON table_name (column_name);
```

### Add Foreign Key
```sql
ALTER TABLE child_table
ADD CONSTRAINT fk_child_parent
FOREIGN KEY (parent_id) REFERENCES parent_table (id);
```
