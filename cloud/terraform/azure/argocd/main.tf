module "argocd" {
	argocd_helm_chart_version=var.argocd_helm_chart_version
	argocd_sso_config=var.argocd_sso_config
	infrastructure_api_group="azure.liferay.com"
	source="../../modules/argocd"
}