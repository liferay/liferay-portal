resource "azurerm_federated_identity_credential" "liferay" {
	audience=["api://AzureADTokenExchange"]
	issuer=azurerm_kubernetes_cluster.main.oidc_issuer_url
	name="${var.deployment_name}-liferay-default"
	subject="system:serviceaccount:${local.deployment_namespace}:liferay-default"
	user_assigned_identity_id=azurerm_user_assigned_identity.workload.id
}
resource "azurerm_kubernetes_cluster" "main" {
	automatic_upgrade_channel="stable"
	azure_policy_enabled=true
	dns_prefix=var.deployment_name
	image_cleaner_enabled=true
	local_account_disabled=false
	location=var.region
	name=local.cluster_name
	node_os_upgrade_channel="NodeImage"
	oidc_issuer_enabled=true
	private_cluster_enabled=var.private_cluster
	resource_group_name=azurerm_resource_group.main.name
	sku_tier="Standard"
	tags=local.tags
	workload_identity_enabled=true
	default_node_pool {
		auto_scaling_enabled=true
		max_count=var.max_node_count
		min_count=var.min_node_count
		name="system"
		only_critical_addons_enabled=true
		temporary_name_for_rotation="systemtmp"
		vm_size=var.machine_type
		vnet_subnet_id=azurerm_subnet.main.id
	}
	depends_on=[
		azurerm_role_assignment.cluster_network_contributor,
	]
	dynamic "api_server_access_profile" {
		content {
			authorized_ip_ranges=var.api_authorized_ip_ranges
		}
		for_each=!var.private_cluster && length(var.api_authorized_ip_ranges) > 0 ? [1] : []
	}
	identity {
		identity_ids=[azurerm_user_assigned_identity.cluster.id]
		type="UserAssigned"
	}
	dynamic "monitor_metrics" {
		content {}
		for_each=var.observability_config.enabled ? [1] : []
	}
	network_profile {
		dns_service_ip=local.dns_service_ip
		network_data_plane="cilium"
		network_plugin="azure"
		network_plugin_mode="overlay"
		network_policy="cilium"
		outbound_type="userAssignedNATGateway"
		pod_cidr=var.pod_cidr
		service_cidr=var.service_cidr
	}
	node_provisioning_profile {
		mode="Auto"
	}
}
resource "azurerm_role_assignment" "acr_pull" {
	for_each=var.container_registries
	principal_id=azurerm_kubernetes_cluster.main.kubelet_identity[0].object_id
	role_definition_name="AcrPull"
	scope=each.value.id
}