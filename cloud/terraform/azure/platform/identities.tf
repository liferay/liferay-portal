resource "azurerm_federated_identity_credential" "crossplane_data" {
	audience=["api://AzureADTokenExchange"]
	issuer=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
	name="crossplane-data"
	subject="system:serviceaccount:crossplane-system:crossplane-data"
	user_assigned_identity_id=azurerm_user_assigned_identity.crossplane_data.id
}
resource "azurerm_federated_identity_credential" "crossplane_iam" {
	audience=["api://AzureADTokenExchange"]
	issuer=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
	name="crossplane-iam"
	subject="system:serviceaccount:crossplane-system:crossplane-iam"
	user_assigned_identity_id=azurerm_user_assigned_identity.crossplane_iam.id
}
resource "azurerm_federated_identity_credential" "external_secrets" {
	audience=["api://AzureADTokenExchange"]
	issuer=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
	name="external-secrets"
	subject="system:serviceaccount:${local.external_secrets_service_account.namespace}:${local.external_secrets_service_account.name}"
	user_assigned_identity_id=azurerm_user_assigned_identity.external_secrets.id
}
resource "azurerm_federated_identity_credential" "keda" {
	audience=["api://AzureADTokenExchange"]
	count=local.keda_enabled ? 1 : 0
	issuer=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
	name="keda"
	subject="system:serviceaccount:${var.keda_config.namespace}:${var.keda_config.service_account_name}"
	user_assigned_identity_id=azurerm_user_assigned_identity.keda[0].id
}
resource "azurerm_federated_identity_credential" "observability" {
	audience=["api://AzureADTokenExchange"]
	count=var.observability_config.enabled ? 1 : 0
	issuer=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
	name="grafana"
	subject="system:serviceaccount:observability:grafana"
	user_assigned_identity_id=azurerm_user_assigned_identity.observability[0].id
}
resource "azurerm_federated_identity_credential" "observability_alloy" {
	audience=["api://AzureADTokenExchange"]
	count=var.observability_config.enabled ? 1 : 0
	issuer=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
	name="alloy"
	subject="system:serviceaccount:observability:liferay-alloy"
	user_assigned_identity_id=azurerm_user_assigned_identity.observability[0].id
}
resource "azurerm_role_assignment" "crossplane_data_contributor" {
	principal_id=azurerm_user_assigned_identity.crossplane_data.principal_id
	role_definition_name="Contributor"
	scope=data.azurerm_resource_group.liferay.id
}
resource "azurerm_role_assignment" "crossplane_iam_managed_identity_contributor" {
	principal_id=azurerm_user_assigned_identity.crossplane_iam.principal_id
	role_definition_name="Managed Identity Contributor"
	scope=data.azurerm_resource_group.liferay.id
}
resource "azurerm_role_assignment" "crossplane_iam_rbac_administrator" {
	condition=local.crossplane_iam_role_assignment_condition
	condition_version="2.0"
	principal_id=azurerm_user_assigned_identity.crossplane_iam.principal_id
	principal_type="ServicePrincipal"
	role_definition_name="Role Based Access Control Administrator"
	scope=data.azurerm_resource_group.liferay.id
}
resource "azurerm_role_assignment" "external_secrets_vault" {
	count=local.default_azure_key_vault_enabled ? 1 : 0
	principal_id=azurerm_user_assigned_identity.external_secrets.principal_id
	role_definition_name="Key Vault Secrets User"
	scope=data.azurerm_key_vault.liferay[0].id
}
resource "azurerm_role_assignment" "keda_monitoring_data_reader" {
	count=local.keda_enabled ? 1 : 0
	principal_id=azurerm_user_assigned_identity.keda[0].principal_id
	role_definition_name="Monitoring Data Reader"
	scope=azurerm_monitor_workspace.main[0].id
}
resource "azurerm_role_assignment" "observability_monitoring_data_reader" {
	count=var.observability_config.enabled ? 1 : 0
	principal_id=azurerm_user_assigned_identity.observability[0].principal_id
	role_definition_name="Monitoring Data Reader"
	scope=azurerm_monitor_workspace.main[0].id
}
resource "azurerm_role_assignment" "observability_monitoring_metrics_publisher" {
	count=var.observability_config.enabled ? 1 : 0
	principal_id=azurerm_user_assigned_identity.observability[0].principal_id
	role_definition_name="Monitoring Metrics Publisher"
	scope=azurerm_monitor_data_collection_rule.main[0].id
}
resource "azurerm_user_assigned_identity" "crossplane_data" {
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-crossplane-data"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_user_assigned_identity" "crossplane_iam" {
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-crossplane-iam"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_user_assigned_identity" "external_secrets" {
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-external-secrets"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_user_assigned_identity" "keda" {
	count=local.keda_enabled ? 1 : 0
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-keda"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_user_assigned_identity" "observability" {
	count=var.observability_config.enabled ? 1 : 0
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-observability"
	resource_group_name=local.resource_group_name
	tags=local.tags
}