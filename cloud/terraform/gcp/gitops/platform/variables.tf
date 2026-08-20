variable "argo_workflows_helm_chart_version" {
	type=string
}
variable "argo_workflows_namespace" {
	default="argo-workflows-system"
	type=string
}
variable "argocd_helm_chart_version" {
	type=string
}
variable "argocd_namespace" {
	default="argocd-system"
	type=string
}
variable "crossplane_helm_chart_version" {
	type=string
}
variable "crossplane_namespace" {
	default="crossplane-system"
	type=string
}
variable "deployment_name" {
	type=string
}
variable "external_secrets_helm_chart_version" {
	type=string
}
variable "external_secrets_namespace" {
	default="external-secrets-system"
	type=string
}
variable "gateway_namespace" {
	default="envoy-gateway-system"
	type=string
}
variable "keda_enabled" {
	default=false
	type=bool
}
variable "keda_helm_chart_version" {
	type=string
}
variable "keda_namespace" {
	default="keda-system"
	type=string
}
variable "observability_config" {
	default={}
	type=object(
		{
			enabled=optional(bool, false)
			namespace=optional(string, "observability")
		}
	)
}
variable "project_id" {
	type=string
}
variable "region" {
	type=string
}