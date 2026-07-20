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