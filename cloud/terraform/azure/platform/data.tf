data "azurerm_client_config" "current" {}
data "azurerm_key_vault" "liferay" {
	count=local.default_azure_key_vault_enabled ? 1 : 0
	lifecycle {
		postcondition {
			condition=self.rbac_authorization_enabled
			error_message="The key vault ${self.name} uses the access policy permission model, but the Liferay platform grants vault access through Azure RBAC roles, so the External Secrets operator would be denied access."
		}
	}
	name=var.cluster_secret_store.key_vault.name
	resource_group_name=var.cluster_secret_store.key_vault.resource_group_name
}
data "azurerm_kubernetes_cluster" "aks" {
	name="${var.deployment_name}-aks"
	resource_group_name=local.resource_group_name
}
data "azurerm_resource_group" "liferay" {
	name=local.resource_group_name
}
data "azurerm_role_definition" "backup_operator" {
	name="Backup Operator"
}
data "azurerm_role_definition" "key_vault_crypto_service_encryption_user" {
	name="Key Vault Crypto Service Encryption User"
}
data "azurerm_role_definition" "storage_account_backup_contributor" {
	name="Storage Account Backup Contributor"
}
data "azurerm_role_definition" "storage_blob_data_contributor" {
	name="Storage Blob Data Contributor"
}
data "azurerm_role_definition" "storage_blob_data_reader" {
	name="Storage Blob Data Reader"
}