variable "deployment_name" {
	default="liferay-self-hosted"
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
	default="gp3"
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
}