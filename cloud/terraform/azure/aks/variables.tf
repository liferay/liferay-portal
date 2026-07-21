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
variable "deployment_name" {
	type=string
}
variable "machine_type" {
	default="Standard_D4s_v4"
	type=string
}
variable "observability_config" {
	default={}
	type=object({
		enabled=optional(bool, false)
		namespace=optional(string, "observability")
	})
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