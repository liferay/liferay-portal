variable "arn_partition" {
	default="aws"
	type=string
}
variable "deployment_name" {
	type=string
	validation {
		condition=can(regex("^[a-z][a-z0-9-]{2,23}$", var.deployment_name))
		error_message="The variable \"deployment_name\" must be 3-24 characters, start with a lowercase letter, and contain only lowercase letters, numbers, and hyphens."
	}
}
variable "eks_allow_public_access" {
	default=true
	type=bool
}
variable "eks_api_additional_allowed_cidr_blocks" {
	default=[]
	type=list(string)
	validation {
		condition=alltrue([for cidr in var.eks_api_additional_allowed_cidr_blocks : can(cidrhost(cidr, 0))])
		error_message="The variable \"eks_api_additional_allowed_cidr_blocks\" must contain valid CIDR blocks."
	}
}
variable "max_availability_zones" {
	default=2
	type=number
}
variable "private_subnets" {
	default=null
	type=list(string)
}
variable "public_subnets" {
	default=null
	type=list(string)
}
variable "region" {
	type=string
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
	type=string
}