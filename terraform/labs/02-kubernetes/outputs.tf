output "namespace_created" {
  description = "Nome do namespace criado pelo Terraform"
  value       = kubernetes_namespace_v1.lab.metadata[0].name
}

output "devflow_namespace_uid" {
  description = "Identificador do namespace DevFlow"
  value       = data.kubernetes_namespace_v1.devflow_existing.metadata[0].uid
}