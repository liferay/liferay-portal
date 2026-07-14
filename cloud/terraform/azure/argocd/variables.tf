variable "argocd_helm_chart_version" {
	type=string
}
variable "argocd_namespace" {
	default="argocd-system"
	type=string
}