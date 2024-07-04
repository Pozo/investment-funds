resource "aws_ecs_cluster" "investment_funds_cluster" {
  name = var.ecs_cluster_name

}

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

resource "aws_iam_role" "investmentfunds-ecs-role" {
  name               = "investmentfunds-ecs-role"
  assume_role_policy = <<EOF
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "ecs-tasks.amazonaws.com"
        },
        "Action": "sts:AssumeRole"
      }
    ]
  }
  EOF
}

resource "aws_iam_policy_attachment" "ecs-ec2-policy-attachment" {
  name       = "ecs-ec2-policy-attachment"
  roles      = [aws_iam_role.investmentfunds-role.name]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_role_policy_attachment" "ssm_role_policy_attachment" {
  role       = aws_iam_role.investmentfunds-role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_policy_attachment" "ecs_instance_policy_attachment" {
  name       = "ecs-instance-policy-attachment"
  roles      = [aws_iam_role.investmentfunds-ecs-role.name]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecs_task_definition" "investment-funds" {
  family                = "service"
  network_mode          = "bridge"
  execution_role_arn    = aws_iam_role.investmentfunds-ecs-role.arn
  container_definitions = jsonencode([
    {
      name        = var.ecs_api_container_name
      image       = "${var.ecr_repository}/investmentfunds/api"
      cpu         = 1024
      memory      = 2622
      essential   = true
      environment = [
        {
          name  = "JAVA_OPTS"
          value = "-Xmx2620m -Xms2620m"
        }
      ],
      portMappings = [
        {
          hostPort      = 80
          containerPort = 8080
        }
      ],
      links = ["investmentfunds-redis"]
    },
    {
      name         = "investmentfunds-redis"
      image        = "redis"
      cpu          = 1024
      memory       = 1310
      essential    = true
      portMappings = [
        {
          hostPort      = 0
          containerPort = 6379
        }
      ]
    }
  ])
}

resource "aws_ecs_service" "investment-funds" {
  name                               = var.ecs_service_name
  cluster                            = aws_ecs_cluster.investment_funds_cluster.id
  task_definition                    = aws_ecs_task_definition.investment-funds.arn
  desired_count                      = 1
  launch_type                        = "EC2"
  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100
}

resource "aws_iam_instance_profile" "investmentfunds_profile" {
  name = "investmentfunds-profile"
  role = aws_iam_role.investmentfunds-role.name
}

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

resource "aws_route_table" "investmentfunds_route_table" {
  vpc_id = aws_vpc.investmentfunds-vpc.id
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
  name        = "investmentfunds-http-tg"
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

resource "aws_lb_target_group_attachment" "http-attachment" {
  target_group_arn = aws_lb_target_group.investmentfunds_http_target_group.arn
  target_id        = aws_instance.investmentfunds_ecs_instance.id
}

resource "aws_lb_listener" "http_listener" {
  load_balancer_arn = aws_lb.investmentfunds-external-alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_listener" "https_listener" {
  load_balancer_arn = aws_lb.investmentfunds-external-alb.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS-1-2-Ext-2018-06"
  certificate_arn   = var.certificate-arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.investmentfunds_http_target_group.arn
  }
}

resource "aws_route53_record" "alias_route53_record" {
  zone_id = var.route53_zone_id
  name    = var.route53_domain
  type    = "A"

  alias {
    name                   = aws_lb.investmentfunds-external-alb.dns_name
    zone_id                = aws_lb.investmentfunds-external-alb.zone_id
    evaluate_target_health = true
  }
}

resource "aws_route53_record" "alias_route53_record_www" {
  zone_id = var.route53_zone_id
  name = "www.${var.route53_domain}" # Replace with your name/domain/subdomain
  type    = "A"

  alias {
    name                   = aws_lb.investmentfunds-external-alb.dns_name
    zone_id                = aws_lb.investmentfunds-external-alb.zone_id
    evaluate_target_health = true
  }
}

resource "aws_security_group" "investmentfunds-sg" {
  name        = "investmentfunds-sg"
  description = "Allow HTTP and HTTPS access"
  vpc_id      = aws_vpc.investmentfunds-vpc.id

  ingress {
    from_port   = 443
    to_port     = 443
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

resource "aws_instance" "investmentfunds_ecs_instance" {
  // amzn-ami-2018.03.20240319-amazon-ecs-optimized
  ami                  = "ami-0f667aa009598db39"
  instance_type        = "t2.medium"
  iam_instance_profile = aws_iam_instance_profile.investmentfunds_profile.name
  subnet_id            = aws_subnet.investmentfunds-subnet-a.id
  security_groups      = [aws_security_group.investmentfunds-sg.id]
  associate_public_ip_address = true // TODO NAT
  user_data            = templatefile("init.sh.tfpl", {
    ecs_cluster_name = aws_ecs_cluster.investment_funds_cluster.name
  })
  lifecycle {
    ignore_changes = all
  }
  tags = {
    Name = "investmentfunds"
  }
}
