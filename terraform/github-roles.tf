resource "aws_iam_openid_connect_provider" "github" {
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
}

# build docker images and push them to ECR
resource "aws_iam_role" "github_build_actions" {
  name               = "github-build-actions-role"
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.github.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringLike = {
            "token.actions.githubusercontent.com:sub" : "repo:${var.github_user}/${var.github_repository}:*"
          },
          StringEquals = {
            "token.actions.githubusercontent.com:aud" : "sts.amazonaws.com"
          }
        }
      }
    ]
  })
}

resource "aws_iam_policy" "github_build_actions_policy" {
  name   = "github-build-actions-policy"
  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:CompleteLayerUpload",
          "ecr:GetDownloadUrlForLayer",
          "ecr:InitiateLayerUpload",
          "ecr:PutImage",
          "ecr:UploadLayerPart",
        ]
        Resource = [
          aws_ecr_repository.investmentfunds-api.arn
        ]
      },
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken"
        ]
        Resource = [
          "*"
        ]

      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_github_build-actions_policy" {
  role       = aws_iam_role.github_build_actions.name
  policy_arn = aws_iam_policy.github_build_actions_policy.arn
}

# deploy images to ECS
resource "aws_iam_role" "github_deploy_actions" {
  name               = "github-deploy-actions-role"
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.github.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringLike = {
            "token.actions.githubusercontent.com:sub" : "repo:${var.github_user}/${var.github_repository}:*"
          },
          StringEquals = {
            "token.actions.githubusercontent.com:aud" : "sts.amazonaws.com"
          }
        }
      }
    ]
  })
}

resource "aws_iam_policy" "github_deploy_actions_policy" {
  name   = "github-deploy-actions-policy"
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "RegisterTaskDefinition",
        "Effect" : "Allow",
        "Action" : [
          "ecs:DescribeTaskDefinition", // for aws ecs describe-task-definition
          "ecs:RegisterTaskDefinition"
        ],
        "Resource" : "*"
      },
      {
        "Sid" : "PassRolesInTaskDefinition",
        "Effect" : "Allow",
        "Action" : [
          "iam:PassRole"
        ],
        "Resource" : [
          aws_iam_role.investmentfunds-ecs-role.arn
        ]
      },
      {
        "Sid" : "DeployService",
        "Effect" : "Allow",
        "Action" : [
          "ecs:UpdateService",
          "ecs:DescribeServices"
        ],
        "Resource" : [
          "arn:aws:ecs:${var.region}:${var.account_id}:service/${var.ecs_cluster_name}/${var.ecs_service_name}"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_github_deploy_actions_policy" {
  role       = aws_iam_role.github_deploy_actions.name
  policy_arn = aws_iam_policy.github_deploy_actions_policy.arn
}