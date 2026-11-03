moved {
  from = aws_vpc.devflow
  to   = module.network.aws_vpc.devflow
}

moved {
  from = aws_subnet.devflow_private
  to   = module.network.aws_subnet.devflow_private
}

moved {
  from = aws_security_group.postgres
  to   = module.security.aws_security_group.postgres
}

moved {
  from = aws_vpc_security_group_ingress_rule.postgres_from_vpc
  to   = module.security.aws_vpc_security_group_ingress_rule.postgres_from_vpc
}