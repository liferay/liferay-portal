module "argocd" {
	argocd_helm_chart_version=var.argocd_helm_chart_version
	argocd_namespace=var.argocd_namespace
	infrastructure_api_group="azure.liferay.com"
	source="../../modules/argocd"
}