variable "name" {
  type = string
}

variable "server_type" {
  type = string
}

variable "image" {
  type = string
}

variable "location" {
  type = string
}

variable "ssh_keys" {
  type    = list(string)
  default = []
}

variable "labels" {
  type    = map(string)
  default = {}
}
