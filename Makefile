# ================================
# development environment (Dev) commands
# ================================

# Start development environment (with hot reload)
dev:
	docker compose -f docker-compose.dev.yml up

# Start development environment in background
dev-d:
	docker compose -f docker-compose.dev.yml up -d

# Stop development environment
dev-down:
	docker compose -f docker-compose.dev.yml down

# Display development environment logs
logs:
	docker compose -f docker-compose.dev.yml logs -f