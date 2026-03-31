variable "cloudflare_api_token" {
  type      = string
  sensitive = true
}

variable "cloudflare_account_id" {
  type = string
}

variable "cloudflare_zone_id" {
  type = string
}

variable "pages_project_name" {
  type    = string
  default = "stock-system"
}

variable "pages_production_branch" {
  type    = string
  default = "main"
}

variable "api_domain" {
  type = string
}

variable "api_ipv4" {
  type = string
}

variable "api_proxied" {
  type    = bool
  default = false
}
