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
	default={}
	type=object({
		provider_hcl=optional(any)
	})
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