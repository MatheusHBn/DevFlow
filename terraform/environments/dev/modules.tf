module "network" {
  source = "../../modules/network"

  vpc_cidr    = "10.20.0.0/16"
  subnet_cidr = "10.20.1.0/24"
}

module "security" {
  source = "../../modules/security"

  vpc_id   = module.network.vpc_id
  vpc_cidr = module.network.vpc_cidr
}