// Terraform configuration for AWS provider
terraform {
  required_providers {
    // AWS provider version ~> 6.27
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.27"
    }
  }

  // Terraform minimum version requirement
  required_version = ">= 1.4"
}

// Configure AWS provider to use ap-northeast-1 region (Tokyo)
provider "aws" {
  region = "ap-northeast-1"
}
