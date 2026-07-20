resource "azurerm_role_assignment" "workload_storage" {
	for_each=toset(local.storage_scopes)
	principal_id=azurerm_user_assigned_identity.workload.principal_id
	role_definition_name="Storage Blob Data Reader"
	scope=each.value
}
resource "azurerm_user_assigned_identity" "workload" {
	location=var.region
	name="${var.deployment_name}-workload-identity"
	resource_group_name=local.resource_group_name
	tags=local.tags
}