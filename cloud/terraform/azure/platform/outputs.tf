output "cluster_identity" {
	value=local.cluster_identity
}
output "cluster_secret_store_provider" {
	value=local.cluster_secret_store_provider
}
output "external_secrets_client_id" {
	value=azurerm_user_assigned_identity.external_secrets.client_id
}