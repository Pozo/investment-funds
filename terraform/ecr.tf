resource "aws_ecr_repository" "investmentfunds-api" {
  image_tag_mutability = "MUTABLE"
  name                 = "investmentfunds/api"
  force_delete         = true

  encryption_configuration {
    encryption_type = "AES256"
  }
  image_scanning_configuration {
    scan_on_push = true
  }
}