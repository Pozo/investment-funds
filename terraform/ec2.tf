# TODO ssm
# TODO cleanup

resource "aws_security_group" "investmentfunds-external-alb-sg" {
  name   = "investmentfunds-external-alb"
  vpc_id = aws_vpc.investmentfunds-vpc.id

  description = "External Load Balancer SecurityGroup"

  egress {
    cidr_blocks = ["0.0.0.0/0"]
    from_port   = "0"
    protocol    = "-1"
    self        = "false"
    to_port     = "0"
  }

  ingress {
    cidr_blocks      = ["0.0.0.0/0"]
    from_port        = 22
    ipv6_cidr_blocks = ["::/0"]
    protocol         = "tcp"
    self             = "false"
    to_port          = 22
  }

  ingress {
    cidr_blocks      = ["0.0.0.0/0"]
    from_port        = "443"
    ipv6_cidr_blocks = ["::/0"]
    protocol         = "tcp"
    self             = "false"
    to_port          = "443"
  }

  ingress {
    cidr_blocks      = ["0.0.0.0/0"]
    from_port        = "80"
    ipv6_cidr_blocks = ["::/0"]
    protocol         = "tcp"
    self             = "false"
    to_port          = "80"
  }

}

resource "aws_vpc" "investmentfunds-vpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "investmentfunds-vpc"
  }
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.investmentfunds-vpc.id
  tags = {
    Name = "investmentfunds-igw"
  }
}

# resource "aws_vpc_attachment" "igw_attachment" {
#   vpc_id              = aws_vpc.investmentfunds-vpc.id
#   internet_gateway_id = aws_internet_gateway.gw.id
# }

resource "aws_route_table" "investmentfunds_route_table" {
  vpc_id = aws_vpc.investmentfunds-vpc.id

  # Define other routes if needed
}

resource "aws_route" "internet_route" {
  route_table_id         = aws_route_table.investmentfunds_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.gw.id
}

resource "aws_subnet" "investmentfunds-subnet-a" {
  vpc_id            = aws_vpc.investmentfunds-vpc.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "eu-central-1a"
}

resource "aws_subnet" "investmentfunds-subnet-b" {
  vpc_id            = aws_vpc.investmentfunds-vpc.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "eu-central-1b"
}

resource "aws_route_table_association" "private_subnet_association-a" {
  subnet_id      = aws_subnet.investmentfunds-subnet-a.id
  route_table_id = aws_route_table.investmentfunds_route_table.id
}

resource "aws_route_table_association" "private_subnet_association-b" {
  subnet_id      = aws_subnet.investmentfunds-subnet-b.id
  route_table_id = aws_route_table.investmentfunds_route_table.id
}

resource "aws_lb" "investmentfunds-external-alb" {
  name               = "investmentfunds-external-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.investmentfunds-external-alb-sg.id]
  subnets            = [aws_subnet.investmentfunds-subnet-a.id, aws_subnet.investmentfunds-subnet-b.id]

  enable_deletion_protection = false
}

resource "aws_lb_target_group" "investmentfunds_http_target_group" {
  name        = "investmentfunds-target-group"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = aws_vpc.investmentfunds-vpc.id
  target_type = "instance"

  health_check {
    path                = "/"
    protocol            = "HTTP"
    port                = "traffic-port"
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 3
    interval            = 30
  }
}

resource "aws_security_group" "investmentfunds-sg" {
  name        = "investmentfunds-sg"
  description = "Allow SSH access"
  vpc_id      = aws_vpc.investmentfunds-vpc.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
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
  ami                         = "ami-0f7204385566b32d0"
  instance_type               = "t2.micro"
  subnet_id                   = aws_subnet.investmentfunds-subnet-a.id
  security_groups             = [aws_security_group.investmentfunds-sg.id]
  iam_instance_profile        = aws_iam_instance_profile.investmentfunds_profile.name
  associate_public_ip_address = true
  key_name                    = "zoltan-aws"
  user_data                   = file("init.sh")

  tags = {
    Name = "investmentfunds"
  }
}

resource "aws_lb_target_group_attachment" "managers-attachment" {
  target_group_arn = aws_lb_target_group.investmentfunds_http_target_group.arn
  target_id        = aws_instance.investmentfunds.id
}

resource "aws_lb_listener" "http_listener" {
  load_balancer_arn = aws_lb.investmentfunds-external-alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.investmentfunds_http_target_group.arn

  }
}
# TODO debug why is it not working with SSH
resource "aws_lb" "investmentfunds-external-network-alb" {
  name               = "investmentfunds-ext-ntwrk-alb"
  internal           = false
  load_balancer_type = "network"
  security_groups    = [aws_security_group.investmentfunds-external-alb-sg.id]
  subnets            = [aws_subnet.investmentfunds-subnet-a.id, aws_subnet.investmentfunds-subnet-b.id]

  enable_deletion_protection = false
}

resource "aws_lb_target_group" "investmentfunds_ssh_target_group" {
  name        = "investmentfunds-ssh-target-group"
  port        = 22
  protocol    = "TCP"
  vpc_id      = aws_vpc.investmentfunds-vpc.id
  target_type = "instance"
}

resource "aws_lb_target_group_attachment" "ssh-managers-attachment" {
  target_group_arn = aws_lb_target_group.investmentfunds_ssh_target_group.arn
  target_id        = aws_instance.investmentfunds.id
}

resource "aws_lb_listener" "ssh_listener" {
  load_balancer_arn = aws_lb.investmentfunds-external-network-alb.arn
  port              = 22
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.investmentfunds_ssh_target_group.arn

  }
}
