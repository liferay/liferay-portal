variable "arn_partition" {
	default="aws"
}
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
variable "is_restoring" {
	default=false
}
variable "liferay_sa_role_arn" {
	type=string
}
variable "liferay_sa_role_name" {
	type=string
}
variable "private_subnet_ids" {
	type=list(string)
}
variable "region" {
	type=string
}