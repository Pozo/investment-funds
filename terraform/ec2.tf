resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "investmentfunds-vpc"
  }
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "investmentfunds-igw"
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "eu-central-1a"
  tags = {
    Name = "investmentfunds-subnet"
  }
}

resource "aws_security_group" "investmentfunds-sg" {
  name        = "investmentfunds-sg"
  description = "Allow SSH access"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "investmentfunds" {
  ami                  = "ami-0f7204385566b32d0"
  instance_type        = "t2.micro"
  subnet_id            = aws_subnet.public.id
  security_groups      = [aws_security_group.investmentfunds-sg.id]
  iam_instance_profile = aws_iam_instance_profile.investmentfunds_profile.name
  tags = {
    Name = "investmentfunds"
  }
}
