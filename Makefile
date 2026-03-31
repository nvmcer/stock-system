DEV_ENV_FILE=infra/env/dev/compose.env
DEV_COMPOSE_FILES=-f infra/compose/base.yml -f infra/compose/dev.yml -f infra/compose/observability.yml

# Start development environment (with hot reload)
dev:
	docker compose --env-file $(DEV_ENV_FILE) $(DEV_COMPOSE_FILES) up --build

# Start development environment without observability
dev-lite:
	docker compose --env-file $(DEV_ENV_FILE) -f infra/compose/base.yml -f infra/compose/dev.yml up --build

# Start development environment in background
dev-d:
	docker compose --env-file $(DEV_ENV_FILE) $(DEV_COMPOSE_FILES) up -d --build

# Stop development environment
dev-down:
	docker compose --env-file $(DEV_ENV_FILE) $(DEV_COMPOSE_FILES) down

# Display development environment logs
logs:
	docker compose --env-file $(DEV_ENV_FILE) $(DEV_COMPOSE_FILES) logs -f
