module "pages_project" {
  source = "../../../modules/cloudflare_pages"

  account_id        = var.cloudflare_account_id
  name              = var.pages_project_name
  production_branch = var.pages_production_branch
}

module "api_dns_record" {
  source = "../../../modules/cloudflare_dns"

  zone_id  = var.cloudflare_zone_id
  name     = var.api_domain
  type     = "A"
  content  = var.api_ipv4
  proxied  = var.api_proxied
  comment  = "Production API entrypoint"
}
