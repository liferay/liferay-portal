variable "deployment_name" {
	type=string
}
variable "region" {
	type=string
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
	type=string
}