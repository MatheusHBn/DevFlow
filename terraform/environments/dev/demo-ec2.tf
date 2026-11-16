data "aws_ssm_parameter" "demo_ami" {
  count = var.enable_aws_demo ? 1 : 0

  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}

resource "aws_security_group" "demo_ec2" {
  count = var.enable_aws_demo ? 1 : 0

  name_prefix = "devflow-demo-ec2-"
  description = "Temporary DevFlow Kubernetes server"
  vpc_id      = module.network.vpc_id

  egress {
    description = "Outbound access for packages and images"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name    = "devflow-demo-ec2-sg"
    Project = "DevFlow"
  }
}

resource "aws_iam_role" "demo_ssm" {
  count = var.enable_aws_demo ? 1 : 0

  name = "DevFlow-Demo-EC2-SSM"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = "sts:AssumeRole"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "demo_ssm" {
  count = var.enable_aws_demo ? 1 : 0

  role       = aws_iam_role.demo_ssm[0].name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "demo" {
  count = var.enable_aws_demo ? 1 : 0

  name = "DevFlow-Demo-EC2-Profile"
  role = aws_iam_role.demo_ssm[0].name
}

resource "aws_instance" "demo" {
  count = var.enable_aws_demo ? 1 : 0

  ami           = data.aws_ssm_parameter.demo_ami[0].value
  instance_type = "m7i-flex.large"

  subnet_id = aws_subnet.demo_public[0].id

  associate_public_ip_address = true

  vpc_security_group_ids = [
    aws_security_group.demo_ec2[0].id
  ]

  iam_instance_profile = aws_iam_instance_profile.demo[0].name

  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required"
  }

  root_block_device {
    volume_size           = 40
    volume_type           = "gp3"
    encrypted             = true
    delete_on_termination = true
  }

  user_data = <<-EOF
    #!/bin/bash
    set -euxo pipefail

    curl -sfL https://get.k3s.io | \
      INSTALL_K3S_EXEC="server --disable traefik --disable servicelb" sh -
  EOF

  tags = {
    Name    = "devflow-demo-k3s"
    Project = "DevFlow"
    Purpose = "temporary-validation"
  }

  depends_on = [
    aws_route.demo_internet,
    aws_route_table_association.demo_public,
    aws_iam_role_policy_attachment.demo_ssm
  ]
}

output "demo_instance_id" {
  value = var.enable_aws_demo ? aws_instance.demo[0].id : null
}

output "demo_public_ip" {
  value = var.enable_aws_demo ? aws_instance.demo[0].public_ip : null
}
