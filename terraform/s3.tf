resource "aws_kms_key" "investmentfunds-key" {
  description             = "This key is used to encrypt bucket objects"
  deletion_window_in_days = 10
}

resource "aws_s3_bucket" "investmentfunds-bucket" {
  bucket = "investmentfunds-bucket"
}

resource "aws_s3_bucket_server_side_encryption_configuration" "investmentfunds-bucket-configuration" {
  bucket = aws_s3_bucket.investmentfunds-bucket.id

  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = aws_kms_key.investmentfunds-key.arn
      sse_algorithm     = "aws:kms"
    }
  }
}