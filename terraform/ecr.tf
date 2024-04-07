resource "aws_ecr_repository" "investmentfunds-api" {
  image_tag_mutability = "MUTABLE"
  name                 = "investmentfunds/api"
  encryption_configuration {
    encryption_type = "AES256"
  }
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "investmentfunds-grabber" {
  image_tag_mutability = "MUTABLE"
  name                 = "investmentfunds/grabber"
  encryption_configuration {
    encryption_type = "AES256"
  }
  image_scanning_configuration {
    scan_on_push = true
  }
}