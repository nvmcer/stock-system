variable "hcloud_token" {
  type      = string
  sensitive = true
}

variable "server_name" {
  type    = string
  default = "stock-system-prod"
}

variable "server_type" {
  type    = string
  default = "cx22"
}

variable "server_image" {
  type    = string
  default = "ubuntu-24.04"
}

variable "server_location" {
  type    = string
  default = "nbg1"
}

variable "ssh_keys" {
  type    = list(string)
  default = []
}
