locals {
	argocd_namespace="argocd-system"
	common_labels={
		"app.kubernetes.io/managed-by"=local.terraform_manager_name
		"liferay.com/project"="liferay-cloud-native"
	}
	terraform_manager_name="liferay-cloud-native-terraform"
}