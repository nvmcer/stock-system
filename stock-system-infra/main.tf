// ECR registry for backend Docker images
module "ecr_backend" {
  source = "./modules/ecr"
  name = "stock-system-backend"
}

// VPC and networking infrastructure
module "vpc" {
  source = "./modules/vpc"

  name = "stock-system-vpc"
  cidr = "10.0.0.0/16"

  // Two availability zones for high availability
  azs = [
    "ap-northeast-1a",
    "ap-northeast-1c"
  ]

  // Public subnets for ALB and NAT gateway
  public_subnets = [
    "10.0.1.0/24",
    "10.0.2.0/24"
  ]

  // Private subnets for ECS and RDS
  private_subnets = [
    "10.0.3.0/24",
    "10.0.4.0/24"
  ]

  project     = "stock-system"
  environment = "prod"
}

resource "aws_security_group" "rds" {
  name        = "rds-sg"
  description = "Allow access from ECS"
  vpc_id      = module.vpc.vpc_id
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    security_groups = [aws_security_group.ecs.id] # ECS SG
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

module "rds" {
  source = "./modules/rds"

  project     = "stock-system"
  environment = "prod"

  db_name  = "stockdb"
  username = "postgres"

  instance_class    = "db.t3.micro"
  allocated_storage = 20

  family = "postgres17"

  security_group_id = aws_security_group.rds.id
  subnet_ids        = module.vpc.private_subnets
}

resource "aws_security_group" "ecs" {
  name   = "ecs-sg"
  vpc_id = module.vpc.vpc_id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    security_groups = [module.alb.alb_sg_id] # ALB SG allows access   
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

module "ecs" {
  source = "./modules/ecs"

  market_data_url = module.lambda_marketdata.function_url

  project     = "stock-system"
  environment = "prod"
  region      = "ap-northeast-1"

  image_url = module.ecr_backend.repository_url

  cpu    = 512
  memory = 1024

  container_port = 8080
  desired_count  = 1

  subnet_ids        = module.vpc.public_subnets
  security_group_id = aws_security_group.ecs.id

  target_group_arn = module.alb.target_group_arn

  db_endpoint = module.rds.address
  db_user     = module.rds.db_user
  db_name     = module.rds.db_name

  db_password_secret_arn = module.rds.db_instance_master_user_secret_arn
}

module "alb" {
  source = "./modules/alb"

  project     = "stock-system"
  environment = "prod"

  vpc_id         = module.vpc.vpc_id
  public_subnets = module.vpc.public_subnets

  target_port = 8080
}

module "lambda_marketdata" {
  source = "./modules/lambda_marketdata"

  finnhub_key = var.finnhub_key
  function_name = "stock-system-marketdata"
  filename      = "../stock-system-marketdata/function.zip"
}

module "frontend" {
  source      = "./modules/frontend"
  project     = "stock-system"
  environment = "prod"
  alb_dns_name = module.alb.alb_dns
}