terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 3.3"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

resource "kubernetes_namespace_v1" "lab" {
  metadata {
    name = var.namespace_name
  }
}

data "kubernetes_namespace_v1" "devflow_existing" {
  metadata {
    name = "devflow"
  }
}