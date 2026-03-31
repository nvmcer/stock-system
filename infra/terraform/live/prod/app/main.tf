locals {
  common_labels = {
    app         = "stock-system"
    environment = "prod"
    managed_by  = "terraform"
  }

  firewall_rules = [
    {
      direction  = "in"
      protocol   = "tcp"
      port       = "22"
      source_ips = ["0.0.0.0/0", "::/0"]
    },
    {
      direction  = "in"
      protocol   = "tcp"
      port       = "80"
      source_ips = ["0.0.0.0/0", "::/0"]
    },
    {
      direction  = "in"
      protocol   = "tcp"
      port       = "443"
      source_ips = ["0.0.0.0/0", "::/0"]
    }
  ]
}

module "app_server" {
  source = "../../../modules/hetzner_server"

  name        = var.server_name
  server_type = var.server_type
  image       = var.server_image
  location    = var.server_location
  ssh_keys    = var.ssh_keys
  labels      = local.common_labels
}

module "app_firewall" {
  source = "../../../modules/hetzner_firewall"

  name       = "${var.server_name}-firewall"
  server_ids = [tostring(module.app_server.id)]
  rules      = local.firewall_rules
}
