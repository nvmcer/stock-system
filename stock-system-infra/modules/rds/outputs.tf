output "endpoint" {
  value = module.rds.db_instance_endpoint
}

output "address" {
  value = module.rds.db_instance_address
}

output "port" {
  value = module.rds.db_instance_port
}

output "db_name" {
  value = var.db_name
}

output "db_user" {
  value = var.username
}

output "db_instance_master_user_secret_arn" {
  description = "The ARN of the master user secret"
  value       = module.rds.db_instance_master_user_secret_arn
}