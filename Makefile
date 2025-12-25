# ================================
# 開発環境（Dev）用コマンド
# ================================

# 開発環境を起動（ホットリロード有り）
dev:
	docker compose -f docker-compose.dev.yml up

# 開発環境をバックグラウンドで起動
dev-d:
	docker compose -f docker-compose.dev.yml up -d

# 開発環境を停止
dev-down:
	docker compose -f docker-compose.dev.yml down

# 開発環境のログを表示
logs:
	docker compose -f docker-compose.dev.yml logs -f


# ================================
# 本番環境（Prod）用コマンド
# ================================

# 本番環境を起動（イメージをビルドしてバックグラウンド起動）
prod:
	docker compose -f docker-compose.prod.yml up -d --build

# 本番環境を停止
prod-down:
	docker compose -f docker-compose.prod.yml down

# 本番環境のログを表示
logs-prod:
	docker compose -f docker-compose.prod.yml logs -f


# ================================
# image build コマンド
# ================================
build:
	docker compose -f docker-compose.prod.yml build