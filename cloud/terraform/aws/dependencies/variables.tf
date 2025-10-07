variable "cluster_name" {
	type=string
}
variable "data_active" {
	default="blue"
	validation {
		condition=contains(["blue", "green"], var.data_active)
		error_message="The active_db_slot must be either 'blue' or 'green'"
	}
}
variable "db_restore_snapshot_identifier" {
	default=null
}
variable "deployment_name" {
	default="liferay-self-hosted"
}
variable "deployment_namespace" {
	default="liferay-system"
}
variable "is_restoring" {
	default=false
}
variable "kube_config_path" {
	default="~/.kube/config"
}
variable "private_subnet_ids" {
	type=list(string)
}
variable "region" {
	type=string
}