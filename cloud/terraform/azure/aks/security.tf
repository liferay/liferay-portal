resource "azurerm_network_security_group" "main" {
	location=var.region
	name="${var.deployment_name}-nsg"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_network_security_rule" "envoy_ingress_managed" {
	access="Allow"
	destination_address_prefix="*"
	destination_port_ranges=["443", "80"]
	direction="Inbound"
	name="${var.deployment_name}-envoy-ingress"
	network_security_group_name=azurerm_network_security_group.main.name
	priority=100
	protocol="Tcp"
	resource_group_name=local.resource_group_name
	source_address_prefix="Internet"
	source_port_range="*"
}