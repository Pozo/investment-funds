resource "aws_iam_role" "investmentfunds-role" {
  name               = "investmentfunds-role"
  assume_role_policy = <<EOF
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "ec2.amazonaws.com"
        },
        "Action": "sts:AssumeRole"
      }
    ]
  }
  EOF
}

resource "aws_iam_instance_profile" "investmentfunds_profile" {
  name = "investmentfunds-profile"
  role = aws_iam_role.investmentfunds-role.name
}

resource "aws_iam_policy" "generic-policy" {
  name = "investmentfunds-generic-policy"
  path = "/"

  policy = file("generic-policy.json")
}

resource "aws_iam_role_policy_attachment" "investmentfunds-generic-policy" {
  policy_arn = aws_iam_policy.generic-policy.arn
  role       = aws_iam_role.investmentfunds-role.name
}

resource "aws_iam_policy" "s3-policy" {
  name = "investmentfunds-s3-policy"
  path = "/"

  policy = templatefile("s3-policy.tfpl", {
    s3-bucket-arn = aws_s3_bucket.investmentfunds-bucket.arn
  })
}

resource "aws_iam_role_policy_attachment" "investmentfunds-s3-policy" {
  policy_arn = aws_iam_policy.s3-policy.arn
  role       = aws_iam_role.investmentfunds-role.name
}