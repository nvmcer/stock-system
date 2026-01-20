variable "project" { type = string }
variable "environment" { type = string }

variable "vpc_id" { type = string }
variable "public_subnets" { type = list(string) }

variable "target_port" { type = number }