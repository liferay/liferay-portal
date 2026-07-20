resource "azurerm_resource_group" "main" {
	location=var.region
	name=var.deployment_name
	tags=local.tags
}