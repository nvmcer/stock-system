output "pages_project_id" {
  value = module.pages_project.id
}

output "pages_subdomain" {
  value = module.pages_project.subdomain
}

output "api_hostname" {
  value = module.api_dns_record.hostname
}
