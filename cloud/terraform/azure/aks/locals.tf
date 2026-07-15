locals {
	cluster_name="${var.deployment_name}-aks"
	deployment_namespace="liferay-system"
	dns_service_ip="10.245.0.10"
	resource_group_name=azurerm_resource_group.main.name
	storage_scopes=[]
	tags={
		deployment=var.deployment_name
		region=var.region
	}
}