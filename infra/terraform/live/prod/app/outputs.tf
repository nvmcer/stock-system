output "server_id" {
  value = module.app_server.id
}

output "server_ipv4" {
  value = module.app_server.ipv4_address
}

output "server_ipv6" {
  value = module.app_server.ipv6_address
}

output "firewall_id" {
  value = module.app_firewall.id
}
