resource "azurerm_role_assignment" "cluster_network_contributor" {
	principal_id=azurerm_user_assigned_identity.cluster.principal_id
	role_definition_name="Network Contributor"
	scope=azurerm_virtual_network.main.id
}
resource "azurerm_user_assigned_identity" "cluster" {
	location=var.region
	name="${var.deployment_name}-cluster-identity"
	resource_group_name=local.resource_group_name
	tags=local.tags
}