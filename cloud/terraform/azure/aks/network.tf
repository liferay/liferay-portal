locals {
	subnet_cidr=cidrsubnet(var.vpc_cidr, 4, 0)
}
resource "azurerm_nat_gateway" "main" {
	location=var.region
	name="${var.deployment_name}-nat"
	resource_group_name=local.resource_group_name
	sku_name="Standard"
	tags=local.tags
}
resource "azurerm_nat_gateway_public_ip_association" "main" {
	nat_gateway_id=azurerm_nat_gateway.main.id
	public_ip_address_id=azurerm_public_ip.nat.id
}
resource "azurerm_network_security_group" "main" {
	location=var.region
	name="${var.deployment_name}-nsg"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_public_ip" "nat" {
	allocation_method="Static"
	location=var.region
	name="${var.deployment_name}-nat-ip"
	resource_group_name=local.resource_group_name
	sku="Standard"
	tags=local.tags
}
resource "azurerm_subnet" "main" {
	address_prefixes=[local.subnet_cidr]
	name="${var.deployment_name}-subnet"
	resource_group_name=local.resource_group_name
	virtual_network_name=azurerm_virtual_network.main.name
}
resource "azurerm_subnet_nat_gateway_association" "main" {
	nat_gateway_id=azurerm_nat_gateway.main.id
	subnet_id=azurerm_subnet.main.id
}
resource "azurerm_subnet_network_security_group_association" "main" {
	network_security_group_id=azurerm_network_security_group.main.id
	subnet_id=azurerm_subnet.main.id
}
resource "azurerm_virtual_network" "main" {
	address_space=[var.vpc_cidr]
	location=var.region
	name="${var.deployment_name}-vnet"
	resource_group_name=local.resource_group_name
	tags=local.tags
}