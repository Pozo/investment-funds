terraform {
  source = "."
}

remote_state {
  backend = "s3"
  config = {
    bucket  = "investment-funds-terraform"
    region  = "eu-central-1"
    key     = "investment-funds/terraform.tfstate"
    encrypt = true
  }
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
}

inputs = {
  region = "eu-central-1"
}