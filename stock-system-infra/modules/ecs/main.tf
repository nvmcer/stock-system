resource "aws_ecs_cluster" "this" {
  name = "${var.project}-${var.environment}-cluster"
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "/ecs/${var.project}-${var.environment}"
  retention_in_days = 7
}

# Task Execution Role - allows ECS to pull images and access Secrets Manager
resource "aws_iam_role" "task_execution_role" {
  name = "${var.project}-${var.environment}-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "task_execution_role_policy" {
  role       = aws_iam_role.task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Additional policy for Secrets Manager access (for database password)
resource "aws_iam_role_policy" "task_execution_secrets_policy" {
  name = "${var.project}-${var.environment}-task-execution-secrets"
  role = aws_iam_role.task_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "secretsmanager:GetSecretValue"
      ]
      Resource = var.db_password_secret_arn
    }]
  })
}

# Task Role - allows application to call AWS services (Lambda, etc)
resource "aws_iam_role" "task_role" {
  name = "${var.project}-${var.environment}-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

# Policy for Lambda invocation
resource "aws_iam_role_policy" "task_lambda_policy" {
  name = "${var.project}-${var.environment}-task-lambda"
  role = aws_iam_role.task_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "lambda:InvokeFunction"
      ]
      Resource = "arn:aws:lambda:*:*:function/${var.project}-*"
    }]
  })
}

resource "aws_ecs_task_definition" "this" {
  family                   = "${var.project}-${var.environment}-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.cpu
  memory                   = var.memory
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  task_role_arn            = aws_iam_role.task_role.arn

  container_definitions = jsonencode([
    {
      name      = "app"
      image     = var.image_url
      essential = true

      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
        { name = "MARKET_DATA_URL", value = var.market_data_url },
        { name = "DB_ENDPOINT", value = var.db_endpoint },
        { name = "DB_PORT", value = "5432" },
        { name = "DB_USERNAME", value = var.db_user },
        { name = "DB_NAME", value = var.db_name }
      ]
      secrets = [
        {
          name      = "DB_PASSWORD"
          valueFrom = "${var.db_password_secret_arn}:password::"
        }
      ]
      portMappings = [{
        containerPort = var.container_port
        hostPort      = var.container_port
      }]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.this.name
          awslogs-region        = var.region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "this" {
  name            = "${var.project}-${var.environment}-service"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.this.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.subnet_ids
    security_groups = [var.security_group_id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "app"
    container_port   = var.container_port
  }

  health_check_grace_period_seconds = 60
}