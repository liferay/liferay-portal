variable "argocd_admin_login_enabled" {
	default=true
	type=bool
}
variable "argocd_external_access_config" {
	default=null
	type=object({
		hostname=string
		sso_enabled=optional(bool, false)
		tls_enabled=optional(bool, false)
	})
}
variable "argocd_helm_chart_version" {
	type=string
}
variable "cluster_secret_store_provider_hcl" {
	default=null
	type=any
}
variable "deployment_name" {
	type=string
	validation {
		condition=can(regex("^[a-z][a-z0-9-]{2,17}$", var.deployment_name))
		error_message="The variable \"deployment_name\" must be 3-18 characters, start with a lowercase letter, and contain only lowercase letters, numbers, and hyphens, so the derived \"<deployment_name>-vault\" Key Vault name fits Azure's 24-character limit."
	}
}
variable "region" {
	type=string
}