module "argocd" {
	argocd_admin_login_enabled=var.argocd_admin_login_enabled
	argocd_external_access_config=var.argocd_external_access_config
	argocd_helm_chart_version=var.argocd_helm_chart_version
	depends_on=[kubernetes_manifest.karpenter_node_pool]
	infrastructure_api_group="liferay.com"
	source="../../modules/argocd"
}