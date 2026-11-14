terraform {
  backend "s3" {
    bucket       = "devflow-tfstate-31621-15204"
    key          = "devflow/dev/terraform.tfstate"
    region       = "sa-east-1"
    encrypt      = true
    use_lockfile = true
  }
}