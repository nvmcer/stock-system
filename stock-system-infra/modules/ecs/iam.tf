data "aws_iam_policy_document" "ecs_task_secrets" {
  statement {
    actions = [
      "secretsmanager:GetSecretValue"
    ]

    resources = [
      var.db_password_secret_arn
    ]
  }
}

resource "aws_iam_role_policy" "ecs_task_secrets" {
  name   = "ecs-task-secrets"
  role   = aws_iam_role.task_execution_role.id
  policy = data.aws_iam_policy_document.ecs_task_secrets.json
}