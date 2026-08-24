output "cluster_secret_store_provider" {
	value=local.cluster_secret_store_provider
}
output "deployment_context" {
	value=local.deployment_context
}
output "external_secrets_client_id" {
	value=azurerm_user_assigned_identity.external_secrets.client_id
}
output "keda_identity_client_id" {
	value=try(azurerm_user_assigned_identity.keda[0].client_id, "")
}
output "keda_service_account_namespace" {
	value=local.keda_enabled ? var.keda_config.namespace : ""
}
output "observability_identity_client_id" {
	value=try(azurerm_user_assigned_identity.observability[0].client_id, "")
}
output "prometheus_data_collection_rule_id" {
	value=try(azurerm_monitor_data_collection_rule.main[0].immutable_id, "")
}
output "prometheus_metrics_ingestion_endpoint" {
	value=try(azurerm_monitor_data_collection_endpoint.main[0].metrics_ingestion_endpoint, "")
}
output "prometheus_workspace_endpoint" {
	value=try(azurerm_monitor_workspace.main[0].query_endpoint, "")
}