variable "arn_partition" {
	default="aws"
}
variable "deployment_name" {
	default="liferay-self-hosted"
	validation {
		condition=can(regex("^[a-z0-9-]*$", var.deployment_name))
		error_message="The deployment_name must contain only lowercase letters, numbers, and hyphens."
	}
}
variable "deployment_namespace" {
	default="liferay-system"
	validation {
		condition=can(regex("^[a-z0-9-]*$", var.deployment_namespace))
		error_message="The deployment_namespace must contain only lowercase letters, numbers, and hyphens."
	}
}
variable "ecr_repositories" {
	type=map(object({ arn=string, url=string }))
	default={}
}
variable "node_group_ami_type" {
	default="AL2023_x86_64_STANDARD"
}
variable "node_group_desired_size" {
	default=2
}
variable "node_group_max_size" {
	default=2
}
variable "node_group_min_size" {
	default=2
}
variable "node_instance_type" {
	default="t3.xlarge"
}
variable "private_subnets" {
	default=["10.0.1.0/24", "10.0.2.0/24"]
}
variable "public_subnets" {
	default=["10.0.101.0/24", "10.0.102.0/24"]
}
variable "region" {
	default="us-west-2"
}
variable "root_volume_size" {
	default=20
}
variable "root_volume_type" {
	default="gp2"
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
}