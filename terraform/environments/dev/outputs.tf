output "aws_account_id" {
  description = "Identificador da conta AWS autenticada"
  value       = data.aws_caller_identity.current.account_id
}