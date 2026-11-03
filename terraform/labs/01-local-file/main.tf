terraform {
  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
  }
}

resource "local_file" "devflow_info" {
  filename = "${path.module}/devflow-info.txt"

  content = <<-EOT
  Projeto: DevFlow
  Infraestrutura gerenciada pelo Terraform
  Ambiente: desenvolvimento
  Status: Aprendendo Infrastructure as Code
EOT
}