variable "additional_values" {
	default=[]
	type=list(string)
}
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
variable "infrastructure_api_group" {
	type=string
}