mock_provider "azurerm" {
	override_during=plan
	source="./tests/mocks"
}
run "should_create_ingestion_resources_when_enabled" {
	assert {
		condition=length(azurerm_monitor_workspace.main) == 1
		error_message="An Azure Monitor workspace must be created when observability is enabled"
	}
	assert {
		condition=length(azurerm_monitor_data_collection_endpoint.main) == 1
		error_message="A data collection endpoint must be created when observability is enabled"
	}
	assert {
		condition=length(azurerm_monitor_data_collection_rule.main) == 1
		error_message="A data collection rule must be created when observability is enabled"
	}
	assert {
		condition=length(azurerm_monitor_data_collection_rule_association.main) == 1
		error_message="A data collection rule association must be created when observability is enabled"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_disable_ingestion_by_default" {
	assert {
		condition=length(azurerm_monitor_workspace.main) == 0
		error_message="No Azure Monitor workspace must be created when observability is disabled"
	}
	assert {
		condition=length(azurerm_monitor_data_collection_endpoint.main) == 0 && length(azurerm_monitor_data_collection_rule.main) == 0
		error_message="No data collection resources must be created when observability is disabled"
	}
	assert {
		condition=length(azurerm_monitor_data_collection_rule_association.main) == 0
		error_message="No data collection rule association must be created when observability is disabled"
	}
	assert {
		condition=output.prometheus_workspace_endpoint == "" && output.prometheus_workspace_id == ""
		error_message="Prometheus workspace outputs must be empty when observability is disabled"
	}
	command=plan
}
run "should_name_ingestion_resources_when_enabled" {
	assert {
		condition=azurerm_monitor_workspace.main[0].name == "liferay-test-amw"
		error_message="The Azure Monitor workspace name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_monitor_data_collection_endpoint.main[0].name == "liferay-test-prometheus-dce"
		error_message="The data collection endpoint name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_monitor_data_collection_rule.main[0].name == "liferay-test-prometheus-dcr"
		error_message="The data collection rule name must be derived from deployment_name"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_route_metrics_to_the_workspace" {
	assert {
		condition=contains(azurerm_monitor_data_collection_rule.main[0].data_flow[0].streams, "Microsoft-PrometheusMetrics")
		error_message="The data collection rule must forward the Microsoft-PrometheusMetrics stream"
	}
	assert {
		condition=one(azurerm_monitor_data_collection_rule.main[0].destinations[0].monitor_account).monitor_account_id == azurerm_monitor_workspace.main[0].id
		error_message="The data collection rule must send metrics to the Azure Monitor workspace"
	}
	assert {
		condition=azurerm_monitor_data_collection_rule.main[0].data_collection_endpoint_id == azurerm_monitor_data_collection_endpoint.main[0].id
		error_message="The data collection rule must reference the Prometheus data collection endpoint"
	}
	assert {
		condition=azurerm_monitor_data_collection_rule_association.main[0].target_resource_id == azurerm_kubernetes_cluster.main.id
		error_message="The data collection rule association must target the AKS cluster"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
variables {
	deployment_name="liferay-test"
	region="eastus"
}