locals {
	argocd_external_url=var.argocd_external_access_config == null ? null : "${var.argocd_external_access_config.tls_enabled ? "https" : "http"}://${var.argocd_external_access_config.hostname}"
	argocd_namespace="argocd-system"
	argocd_sso_enabled=try(var.argocd_external_access_config.sso_enabled, false)
	common_labels={
		"app.kubernetes.io/managed-by"=local.terraform_manager_name
		"liferay.com/project"="liferay-cloud-native"
	}
	terraform_manager_name="liferay-cloud-native-terraform"
}