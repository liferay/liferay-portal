locals {
	resource_group_name=azurerm_resource_group.main.name
	storage_scopes=[]
	tags={
		deployment=var.deployment_name
		region=var.region
	}
}