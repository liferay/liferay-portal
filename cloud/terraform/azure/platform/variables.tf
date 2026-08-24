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
variable "cluster_secret_store" {
	type=object({
		key_vault=optional(object({
			name=string
			resource_group_name=string
		}))
		provider_hcl=optional(any)
	})
	validation {
		condition=(var.cluster_secret_store.key_vault == null) != (var.cluster_secret_store.provider_hcl == null)
		error_message="The variable \"cluster_secret_store\" must set either \"key_vault\" (an existing Azure key vault for backing the cluster secret store) or \"provider_hcl\" (a custom External Secrets provider)."
	}
}
variable "deployment_name" {
	type=string
}
variable "keda_config" {
	default={}
	type=object({
		enabled=optional(bool, false)
		namespace=optional(string, "keda-system")
		service_account_name=optional(string, "keda-operator")
	})
}
variable "observability_config" {
	default={}
	type=object({
		enabled=optional(bool, false)
	})
}
variable "region" {
	type=string
}