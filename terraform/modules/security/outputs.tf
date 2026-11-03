output "postgres_security_group_id" {
  description = "ID do Security Group do PostgreSQL"
  value       = aws_security_group.postgres.id
}