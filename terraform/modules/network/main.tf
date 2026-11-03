resource "aws_vpc" "devflow" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name    = "devflow-lab-vpc"
    Project = "DevFlow"
  }
}

resource "aws_subnet" "devflow_private" {
  vpc_id                  = aws_vpc.devflow.id
  cidr_block              = var.subnet_cidr
  map_public_ip_on_launch = false

  tags = {
    Name    = "devflow-lab-private-subnet"
    Project = "DevFlow"
  }
}