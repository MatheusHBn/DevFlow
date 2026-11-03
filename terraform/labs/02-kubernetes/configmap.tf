resource "kubernetes_config_map_v1" "app_config" {
  metadata {
    name      = "terraform-app-config"
    namespace = kubernetes_namespace_v1.lab.metadata[0].name
  }

  data = {
    APP_ENV  = "development"
    APP_NAME = "DevFlow Terraform Lab"
  }
}