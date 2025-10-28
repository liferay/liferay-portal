module "vpc" {
	azs=slice(data.aws_availability_zones.available.names, 0, 3)
	cidr=var.vpc_cidr
	enable_dns_hostnames=true
	enable_nat_gateway=true
	name="${var.deployment_name}-vpc"
	private_subnet_tags={
		"kubernetes.io/role/internal-elb"=1
	}
	private_subnets=var.private_subnets
	public_subnet_tags={
		"kubernetes.io/role/elb"=1
	}
	public_subnets=var.public_subnets
	single_nat_gateway=true
	source="terraform-aws-modules/vpc/aws"
	tags={
		DeploymentName=var.deployment_name
		Name="${var.deployment_name}-vpc"
	}
	version="5.8.1"
}