resource "azurerm_monitor_data_collection_endpoint" "prometheus" {
	count=var.observability_config.enabled ? 1 : 0
	kind="Linux"
	location=var.region
	name="${var.deployment_name}-prometheus-dce"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
resource "azurerm_monitor_data_collection_rule" "prometheus" {
	count=var.observability_config.enabled ? 1 : 0
	data_collection_endpoint_id=azurerm_monitor_data_collection_endpoint.prometheus[0].id
	description="Forwards the Prometheus metrics scraped by the AKS managed collector to the Azure Monitor workspace."
	kind="Linux"
	location=var.region
	name="${var.deployment_name}-prometheus-dcr"
	resource_group_name=local.resource_group_name
	tags=local.tags
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
}
resource "azurerm_monitor_data_collection_rule_association" "prometheus" {
	count=var.observability_config.enabled ? 1 : 0
	data_collection_rule_id=azurerm_monitor_data_collection_rule.prometheus[0].id
	description="Associates the Prometheus data collection rule with the cluster. Deleting this association stops metric collection."
	name="${var.deployment_name}-prometheus"
	target_resource_id=azurerm_kubernetes_cluster.main.id
}
resource "azurerm_monitor_workspace" "main" {
	count=var.observability_config.enabled ? 1 : 0
	location=var.region
	name="${var.deployment_name}-amw"
	resource_group_name=local.resource_group_name
	tags=local.tags
}
