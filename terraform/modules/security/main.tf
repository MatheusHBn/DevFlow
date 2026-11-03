resource "aws_security_group" "postgres" {
  name        = "devflow-lab-postgres"
  description = "Security Group do PostgresSQL do DevFlow"
  vpc_id      = var.vpc_id

  tags = {
    Name    = "devflow-lab-postgres"
    Project = "DevFlow"
  }
}

resource "aws_vpc_security_group_ingress_rule" "postgres_from_vpc" {
  security_group_id = aws_security_group.postgres.id

  cidr_ipv4   = var.vpc_cidr
  from_port   = 5432
  to_port     = 5432
  ip_protocol = "tcp"

  description = "Permitir PostgreSQL somente da VPC"
}