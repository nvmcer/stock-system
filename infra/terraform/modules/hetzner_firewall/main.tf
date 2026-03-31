resource "hcloud_firewall" "this" {
  name = var.name

  dynamic "rule" {
    for_each = var.rules
    content {
      direction  = rule.value.direction
      protocol   = rule.value.protocol
      port       = rule.value.port
      source_ips = rule.value.source_ips
    }
  }
}

resource "hcloud_firewall_attachment" "this" {
  firewall_id = hcloud_firewall.this.id
  server_ids  = var.server_ids
}
