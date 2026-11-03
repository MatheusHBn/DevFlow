variable "vpc_id" {
  description = "ID da VPC onde o Security Group sera criado"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR permitido para acessar o PostgreSQL"
  type        = string
}