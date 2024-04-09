#!/bin/bash
# Update package lists
yum update -y

# Install yum-utils (required for dnf)
yum install -y yum-utils
# Install Docker
yum install -y docker

# Enable and start Docker service
systemctl enable docker
systemctl start docker

usermod -a -G docker ec2-user
