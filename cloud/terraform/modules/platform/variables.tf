variable "argocd_namespace" {
	default="argocd-system"
	type=string
}
variable "platform_helm_chart_config" {
	default={}
	type=object({
		chart_name=optional(string, "liferay-platform")
		chart_url=optional(string, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-platform")
		path=optional(string, null)
	})
}
variable "platform_helm_chart_version" {
	type=string
}
variable "platform_helm_values" {
	type=any
}