output "vpc_id" {
  description = "ID da VPC"
  value       = aws_vpc.devflow.id
}

output "vpc_cidr" {
  description = "CIDR da VPC"
  value       = aws_vpc.devflow.cidr_block
}

output "subnet_id" {
  description = "ID da subnet privada"
  value       = aws_subnet.devflow_private.id
}