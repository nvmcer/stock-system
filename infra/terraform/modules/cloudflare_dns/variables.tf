variable "zone_id" {
  type = string
}

variable "name" {
  type = string
}

variable "type" {
  type = string
}

variable "content" {
  type = string
}

variable "ttl" {
  type    = number
  default = 1
}

variable "proxied" {
  type    = bool
  default = false
}

variable "comment" {
  type    = string
  default = null
}
