variable "name" {
  type = string
}

variable "server_ids" {
  type    = list(string)
  default = []
}

variable "rules" {
  type = list(object({
    direction  = string
    protocol   = string
    port       = string
    source_ips = list(string)
  }))
}
