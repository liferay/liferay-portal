variable "additional_values" {
	default=[]
	type=list(string)
}
variable "argocd_helm_chart_version" {
	type=string
}
variable "argocd_namespace" {
	default="argocd-system"
	type=string
}
variable "argocd_sso_config" {
	default={}
	type=object({
		enable_admin_login=optional(bool, true)
		enable_saml_sso=optional(bool, false)
	})
}
variable "infrastructure_api_group" {
	type=string
}