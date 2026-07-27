mock_resource "azurerm_nat_gateway" {
	defaults={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Network/natGateways/liferay-test-nat"
	}
}
mock_resource "azurerm_network_security_group" {
	defaults={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Network/networkSecurityGroups/liferay-test-nsg"
	}
}
mock_resource "azurerm_public_ip" {
	defaults={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Network/publicIPAddresses/liferay-test-nat-ip"
	}
}
mock_resource "azurerm_subnet" {
	defaults={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Network/virtualNetworks/liferay-test-vnet/subnets/liferay-test-subnet"
	}
}
mock_resource "azurerm_user_assigned_identity" {
	defaults={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.ManagedIdentity/userAssignedIdentities/liferay-test-identity"
		principal_id="00000000-0000-0000-0000-000000000001"
	}
}
mock_resource "azurerm_virtual_network" {
	defaults={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Network/virtualNetworks/liferay-test-vnet"
	}
}
override_resource {
	override_during=plan
	target=azurerm_kubernetes_cluster.main
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.ContainerService/managedClusters/liferay-test-aks"
	}
}
override_resource {
	override_during=plan
	target=azurerm_monitor_data_collection_endpoint.main[0]
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Insights/dataCollectionEndpoints/liferay-test-prometheus-dce"
	}
}
override_resource {
	override_during=plan
	target=azurerm_monitor_workspace.main[0]
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Monitor/accounts/liferay-test-amw"
	}
}