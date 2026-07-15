variable "api_authorized_ip_ranges" {
	default=[]
	description="CIDRs allowed to reach the API server. Only applied when private_cluster is false."
	type=list(string)
}
variable "container_registries" {
	default={}
	description="Azure Container Registries the kubelet identity may pull from (the ECR analog)."
	type=map(object({
		id=string
	}))
}
variable "deployment_name" {
	type=string
}
variable "machine_type" {
	default="Standard_D4s_v5"
	type=string
}
variable "max_node_count" {
	default=4
	type=number
}
variable "min_node_count" {
	default=1
	type=number
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
	description="Overlay pod CIDR. Must not overlap vpc_cidr or service_cidr."
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
	description="Service CIDR. Must not overlap vpc_cidr or pod_cidr."
	type=string
}
variable "vpc_cidr" {
	default="10.0.0.0/16"
	type=string
}