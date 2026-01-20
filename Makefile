# Detect operating system
ifeq ($(OS),Windows_NT)
    RM := del /Q
    MKDIR := mkdir
    RMDIR := rmdir /S /Q
    SLASH := \\
		MVNW := mvnw.cmd
		NPM := npm.cmd
else
    RM := rm -f
    MKDIR := mkdir -p
    RMDIR := rm -rf
    SLASH := /
		MVNW := ./mvnw
		NPM := npm
endif

# Load environment variables from .env file if it exists
ifneq ("$(wildcard .env)","")
    include .env
endif
# Export environment variables
export REGION ECR_URL S3_BUCKET BACKEND_DIR FRONTEND_DIR MARKETDATA_DIR ZIP_NAME
export MVNW
export NPM

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

# ================================
# Other commands
# ================================
.PHONY: build-marketdata clean

build-marketdata:
	@echo "🚀 Building $(MARKETDATA_DIR) Linux compatible package..."
	docker run --rm -v "$(CURDIR)/$(MARKETDATA_DIR)":/var/task -w /var/task python:3.12-slim \
		/bin/sh -c "pip install -r requirements.txt -t ./package && \
		cp -r app ./package/ && \
		python -m zipfile -c function.zip ./package/* && \
		rm -rf ./package"
	@echo "✅ Build complete! File saved at: $(MARKETDATA_DIR)/function.zip"

clean:
	@echo "🧹 Cleaning up build artifacts..."
	-$(RM) $(MARKETDATA_DIR)$(SLASH)$(ZIP_NAME)
	@echo "✅ Clean complete!"

.PHONY: deploy-backend deploy-frontend ecr-login

ecr-login:
	@echo "Logging in to Amazon ECR..."
	aws ecr get-login-password --region $(REGION) | docker login --username AWS --password-stdin $(ECR_URL)

deploy-backend: ecr-login
	@echo "🚀 Building Backend JAR..."
	cd $(BACKEND_DIR) && $(MVNW) clean package -DskipTests
	
	@echo "📦 Building Docker Image..."
	docker build -t stock-backend $(BACKEND_DIR)
	
	@echo "🏷️ Tagging and Pushing Image to ECR..."
	docker tag stock-backend:latest $(ECR_URL):latest
	docker push $(ECR_URL):latest
	@echo "✅ Backend deployment to ECR completed!"

deploy-frontend:
	@echo "🚀 Building Frontend Assets..."
	cd $(FRONTEND_DIR) && $(NPM) install && $(NPM) run build
	
	@echo "☁️ Syncing to S3 Bucket..."
	aws s3 sync $(FRONTEND_DIR)/dist s3://$(S3_BUCKET) --delete
	@echo "✅ Frontend deployment to S3 completed!"