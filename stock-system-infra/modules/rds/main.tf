module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "6.3.0"

  identifier = "${var.project}-${var.environment}-db"

  engine            = "postgres"
  engine_version    = "17.4"
  family            = "postgres17"
  instance_class    = var.instance_class
  allocated_storage = var.allocated_storage

  db_name  = var.db_name
  username = var.username
  manage_master_user_password = true
  port     = 5432

  multi_az = false

  vpc_security_group_ids = [var.security_group_id]
  create_db_subnet_group = true
  subnet_ids             = var.subnet_ids

  storage_encrypted = false

  backup_retention_period = 1
  skip_final_snapshot     = false

  tags = {
    Project = var.project
    Env     = var.environment
  }
}