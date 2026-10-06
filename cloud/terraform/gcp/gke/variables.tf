variable "deletion_protection" {
	default=true
	type=bool
}
variable "deployment_name" {
	type=string
	validation {
		condition=can(regex("^[a-z][a-z0-9-]{2,23}$", var.deployment_name))
		error_message="The variable \"deployment_name\" must be 3-24 characters, start with a lowercase letter, and contain only lowercase letters, numbers, and hyphens."
	}
}
variable "envoy_gateway_helm_chart_version" {
	type=string
}
variable "gateway_namespace" {
	default="envoy-gateway-system"
	type=string
}
variable "gke_security_group" {
	default=null
	type=string
}
variable "machine_type" {
	default="e2-standard-4"
	type=string
}
variable "master_authorized_networks" {
	default=["10.0.0.0/16",]
	type=list(string)
}
variable "master_ipv4_cidr_block" {
	default="172.16.0.0/28"
	type=string
}
variable "max_node_count" {
	default=4
	type=number
}
variable "metrics_scrape_config" {
	default={}
	type=object(
		{
			enabled=optional(bool, true)
			namespace=optional(string, "gmp-system")
		}
	)
}
variable "min_node_count" {
	default=1
	type=number
}
variable "project_id" {
	type=string
}
variable "region" {
	type=string
}
variable "regional_cluster" {
	default=false
	type=bool
}
variable "spot_instances" {
	default=false
	type=bool
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
	type=string
}
