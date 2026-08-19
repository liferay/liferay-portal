resource "azurerm_monitor_data_collection_endpoint" "main" {
	count=var.observability_config.enabled ? 1 : 0
	kind="Linux"
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-prometheus-dce"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_monitor_data_collection_rule" "main" {
	count=var.observability_config.enabled ? 1 : 0
	data_collection_endpoint_id=azurerm_monitor_data_collection_endpoint.main[0].id
	data_flow {
		destinations=["MonitoringAccount1"]
		streams=["Microsoft-PrometheusMetrics"]
	}
	data_sources {
		prometheus_forwarder {
			name="PrometheusDataSource"
			streams=["Microsoft-PrometheusMetrics"]
		}
	}
	destinations {
		monitor_account {
			monitor_account_id=azurerm_monitor_workspace.main[0].id
			name="MonitoringAccount1"
		}
	}
	kind="Linux"
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-prometheus-dcr"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_monitor_data_collection_rule_association" "main" {
	count=var.observability_config.enabled ? 1 : 0
	data_collection_rule_id=azurerm_monitor_data_collection_rule.main[0].id
	name="${var.deployment_name}-prometheus"
	target_resource_id=data.azurerm_kubernetes_cluster.aks.id
}
resource "azurerm_monitor_workspace" "main" {
	count=var.observability_config.enabled ? 1 : 0
	location=data.azurerm_resource_group.liferay.location
	name="${var.deployment_name}-amw"
	resource_group_name=local.resource_group_name
	tags=local.tags
}