variable "project" { type = string }
variable "environment" { type = string }
variable "region" { type = string }

variable "image_url" { type = string }

variable "cpu" { type = number }
variable "memory" { type = number }
variable "container_port" { type = number }

variable "desired_count" { type = number }

variable "subnet_ids" { type = list(string) }
variable "security_group_id" { type = string }

variable "target_group_arn" { type = string }

variable "db_endpoint" { type = string }
variable "db_user" { type = string }
variable "db_name" { type = string }

variable "db_password_secret_arn" { type = string }

variable "market_data_url" { type = string }