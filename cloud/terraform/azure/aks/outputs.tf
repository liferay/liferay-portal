output "cluster_name" {
	value=azurerm_kubernetes_cluster.main.name
}
output "deployment_name" {
	value=var.deployment_name
}
output "kubelet_identity_object_id" {
	value=one(azurerm_kubernetes_cluster.main.kubelet_identity[*].object_id)
}
output "node_resource_group" {
	value=azurerm_kubernetes_cluster.main.node_resource_group
}
output "observability_identity_client_id" {
	value=try(azurerm_user_assigned_identity.observability[0].client_id, "")
}
output "oidc_issuer_url" {
	value=azurerm_kubernetes_cluster.main.oidc_issuer_url
}
output "private_subnet_ids" {
	value=[azurerm_subnet.main.id]
}
output "prometheus_workspace_endpoint" {
	value=try(azurerm_monitor_workspace.main[0].query_endpoint, "")
}
output "prometheus_workspace_id" {
	value=try(azurerm_monitor_workspace.main[0].id, "")
}
output "region" {
	value=var.region
}
output "resource_group_name" {
	value=azurerm_resource_group.main.name
}
output "subnet_id" {
	depends_on=[
		azurerm_subnet_nat_gateway_association.main,
		azurerm_subnet_network_security_group_association.main,
	]
	value=azurerm_subnet.main.id
}
output "vnet_id" {
	value=azurerm_virtual_network.main.id
}
output "workload_identity_client_id" {
	value=azurerm_user_assigned_identity.workload.client_id
}
output "workload_identity_id" {
	value=azurerm_user_assigned_identity.workload.id
}