variable "arn_partition" {
	default="aws"
}
variable "demo_mode" {
	default=false
	type=bool
}
variable "deployment_name" {
	type=string
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
variable "private_subnets" {
	default=["10.0.1.0/24", "10.0.2.0/24"]
}
variable "public_subnets" {
	default=["10.0.101.0/24", "10.0.102.0/24"]
}
variable "region" {
	type=string
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
}