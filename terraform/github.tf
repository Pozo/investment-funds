resource "github_actions_secret" "aws_github_docker_build_and_push_role" {
  repository      = var.github_repository
  secret_name     = "AWS_GITHUB_DOCKER_BUILD_AND_PUSH_ROLE"
  plaintext_value = aws_iam_role.github_build_actions.arn
}

resource "github_actions_secret" "aws_github_ecs_deploy_role" {
  repository      = var.github_repository
  secret_name     = "AWS_GITHUB_ECS_DEPLOY_ROLE"
  plaintext_value = aws_iam_role.github_deploy_actions.arn
}

resource "github_actions_secret" "aws_ecr_repository" {
  repository      = var.github_repository
  secret_name     = "AWS_ECR_REPOSITORY"
  plaintext_value = var.ecr_repository
}

resource "github_actions_secret" "aws_region" {
  repository      = var.github_repository
  secret_name     = "AWS_REGION"
  plaintext_value = var.region
}

resource "github_actions_variable" "aws_ecs_cluster_name" {
  repository    = var.github_repository
  variable_name = "AWS_ECS_CLUSTER_NAME"
  value         = aws_ecs_cluster.investment_funds_cluster.name
}

resource "github_actions_variable" "aws_ecs_task_definition_family" {
  repository    = var.github_repository
  variable_name = "AWS_ECS_TASK_DEFINITION_FAMILY"
  value         = aws_ecs_task_definition.investment-funds.family
}

resource "github_actions_variable" "aws_ecs_api_service_name" {
  repository    = var.github_repository
  variable_name = "AWS_ECS_API_SERVICE_NAME"
  value         = aws_ecs_service.investment-funds.name
}

resource "github_actions_variable" "aws_ecs_api_container_name" {
  repository    = var.github_repository
  variable_name = "AWS_ECS_API_CONTAINER_NAME"
  value         = var.ecs_api_container_name
}
