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