variable "argocd_helm_chart_version" {
	type=string
}
variable "argocd_sso_config" {
	default={}
	type=object({
		enable_admin_login=optional(bool, true)
		enable_saml_sso=optional(bool, false)
	})
}