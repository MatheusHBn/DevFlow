
variable "enable_aws_demo" {
  description = "Enable temporary AWS demo infrastructure"
  type        = bool
  default     = false
}

resource "aws_internet_gateway" "demo" {
  count  = var.enable_aws_demo ? 1 : 0
  vpc_id = module.network.vpc_id

  tags = {
    Name    = "devflow-demo-igw"
    Project = "DevFlow"
  }
}

resource "aws_subnet" "demo_public" {
  count                   = var.enable_aws_demo ? 1 : 0
  vpc_id                  = module.network.vpc_id
  cidr_block              = "10.20.2.0/24"
  availability_zone       = "sa-east-1a"
  map_public_ip_on_launch = false

  tags = {
    Name    = "devflow-demo-public-subnet"
    Project = "DevFlow"
  }
}

resource "aws_route_table" "demo_public" {
  count  = var.enable_aws_demo ? 1 : 0
  vpc_id = module.network.vpc_id

  tags = {
    Name    = "devflow-demo-public-rt"
    Project = "DevFlow"
  }
}

resource "aws_route" "demo_internet" {
  count                  = var.enable_aws_demo ? 1 : 0
  route_table_id         = aws_route_table.demo_public[0].id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.demo[0].id
}

resource "aws_route_table_association" "demo_public" {
  count          = var.enable_aws_demo ? 1 : 0
  subnet_id      = aws_subnet.demo_public[0].id
  route_table_id = aws_route_table.demo_public[0].id
}
