variable "function_name" {
  type        = string
  description = "Lambda function name"
}

variable "filename" {
  type        = string
  description = "Path to Lambda zip file"
}

variable "runtime" {
  type        = string
  default     = "python3.12"
}

variable "timeout" {
  type        = number
  default     = 30
}

variable "memory_size" {
  type        = number
  default     = 512
}

variable "finnhub_key" {
  type        = string
  description = "FINNHUB API key for market data access"
}