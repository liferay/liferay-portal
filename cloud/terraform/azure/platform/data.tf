data "azurerm_client_config" "current" {}
data "azurerm_key_vault" "liferay" {
	count=local.default_azure_key_vault_enabled ? 1 : 0
	name=local.key_vault_name
	resource_group_name=local.key_vault_resource_group_name
}
data "azurerm_kubernetes_cluster" "aks" {
	name="${var.deployment_name}-aks"
	resource_group_name=local.resource_group_name
}
data "azurerm_resource_group" "liferay" {
	name=local.resource_group_name
}
data "azurerm_role_definition" "key_vault_crypto_service_encryption_user" {
	name="Key Vault Crypto Service Encryption User"
}
data "azurerm_role_definition" "storage_blob_data_contributor" {
	name="Storage Blob Data Contributor"
}