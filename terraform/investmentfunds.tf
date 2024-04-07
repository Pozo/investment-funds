resource "aws_iam_role" "investmentfunds" {
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
  role = aws_iam_role.investmentfunds.name
}

resource "aws_iam_policy" "investmentfunds-policy" {
  name = "investmentfunds-policy"
  path = "/"

  policy = file("investmentfunds-policy.json")
}

resource "aws_iam_role_policy_attachment" "investmentfunds-policy" {
  policy_arn = aws_iam_policy.investmentfunds-policy.arn
  role       = aws_iam_role.investmentfunds.name
}