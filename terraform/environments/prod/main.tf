terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region  = "sa-east-1"
  profile = "default"
}

module "network" {
  source = "../../modules/network"

  vpc_cidr    = var.vpc_cidr
  subnet_cidr = var.subnet_cidr
}

module "security" {
  source = "../../modules/security"

  vpc_id   = module.network.vpc_id
  vpc_cidr = module.network.vpc_cidr
}