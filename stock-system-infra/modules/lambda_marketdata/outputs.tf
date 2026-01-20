output "function_name" {
  value = aws_lambda_function.this.function_name
}

output "function_arn" {
  value = aws_lambda_function.this.arn
}

output "function_url" {
  description = "The HTTP URL of the Lambda function"
  value       = aws_lambda_function_url.this.function_url
}
