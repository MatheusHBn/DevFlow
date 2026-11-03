resource "kubernetes_deployment_v1" "web" {
  metadata {
    name      = "terraform-web"
    namespace = kubernetes_namespace_v1.lab.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "terraform-web"
      }
    }

    template {
      metadata {
        labels = {
          app = "terraform-web"
        }
      }

      spec {
        container {
          name  = "nginx"
          image = "nginx:stable-alpine-slim"

          port {
            container_port = 80
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map_v1.app_config.metadata[0].name
            }
          }
        }
      }
    }
  }
}