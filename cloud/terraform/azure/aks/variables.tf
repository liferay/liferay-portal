variable "api_authorized_ip_ranges" {
	default=[]
	type=list(string)
}
variable "container_registries" {
	default={}
	type=map(object({
		id=string
	}))
}
variable "database_engine" {
	type=string
	validation {
		condition=contains(["mysql", "postgresql"], var.database_engine)
		error_message="The variable \"database_engine\" must be \"mysql\" or \"postgresql\"."
	}
}
variable "deployment_name" {
	type=string
}
variable "host_encryption_enabled" {
	default=false
	type=bool
}
variable "machine_type" {
	default="Standard_D8s_v3"
	type=string
}
variable "pod_cidr" {
	default="10.244.0.0/16"
	type=string
}
variable "private_cluster" {
	default=false
	type=bool
}
variable "region" {
	type=string
}
variable "service_cidr" {
	default="10.245.0.0/16"
	type=string
}
variable "system_node_count" {
	default=2
	type=number
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
	type=string
}