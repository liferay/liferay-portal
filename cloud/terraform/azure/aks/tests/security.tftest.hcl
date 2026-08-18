mock_provider "azurerm" {
	source="./tests/mocks"
}
override_resource {
	override_during=plan
	target=azurerm_monitor_workspace.main[0]
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Monitor/accounts/liferay-test-amw"
	}
}
run "should_configure_federated_identity_credential" {
	assert {
		condition=azurerm_federated_identity_credential.liferay.name == "liferay-test-liferay-default"
		error_message="The federated identity credential name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_federated_identity_credential.liferay.subject == "system:serviceaccount:liferay-system:liferay-default"
		error_message="The federated identity credential must bind the liferay-default service account in the liferay-system namespace"
	}
	assert {
		condition=one(azurerm_federated_identity_credential.liferay.audience) == "api://AzureADTokenExchange"
		error_message="The federated identity credential must use the Azure AD token exchange audience"
	}
	command=plan
}
run "should_create_cluster_and_workload_identities" {
	assert {
		condition=azurerm_user_assigned_identity.cluster.name == "liferay-test-cluster-identity"
		error_message="The cluster identity name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_user_assigned_identity.workload.name == "liferay-test-workload-identity"
		error_message="The workload identity name must be derived from deployment_name"
	}
	command=plan
}
run "should_create_observability_identity_when_enabled" {
	assert {
		condition=azurerm_user_assigned_identity.observability[0].name == "liferay-test-observability"
		error_message="The observability identity name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_federated_identity_credential.observability[0].subject == "system:serviceaccount:observability:grafana"
		error_message="The observability federated credential must bind the grafana service account in the observability namespace"
	}
	assert {
		condition=one(azurerm_federated_identity_credential.observability[0].audience) == "api://AzureADTokenExchange"
		error_message="The observability federated credential must use the Azure AD token exchange audience"
	}
	assert {
		condition=azurerm_federated_identity_credential.observability_alloy[0].subject == "system:serviceaccount:observability:liferay-alloy"
		error_message="The alloy federated credential must bind the liferay-alloy service account in the observability namespace"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_grant_cluster_network_contributor" {
	assert {
		condition=azurerm_role_assignment.cluster_network_contributor.role_definition_name == "Network Contributor"
		error_message="The cluster identity must be granted the Network Contributor role on the virtual network"
	}
	command=plan
}
run "should_grant_monitoring_data_reader_on_the_workspace" {
	assert {
		condition=azurerm_role_assignment.observability_monitoring_data_reader[0].role_definition_name == "Monitoring Data Reader"
		error_message="The observability identity must be granted the Monitoring Data Reader role to query the workspace"
	}
	assert {
		condition=azurerm_role_assignment.observability_monitoring_data_reader[0].scope == azurerm_monitor_workspace.main[0].id
		error_message="The Monitoring Data Reader grant must be scoped to the Azure Monitor workspace"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_grant_monitoring_metrics_publisher_on_the_data_collection_rule" {
	assert {
		condition=azurerm_role_assignment.observability_monitoring_metrics_publisher[0].role_definition_name == "Monitoring Metrics Publisher"
		error_message="The observability identity must be granted the Monitoring Metrics Publisher role to remote write metrics"
	}
	assert {
		condition=azurerm_role_assignment.observability_monitoring_metrics_publisher[0].scope == azurerm_monitor_data_collection_rule.main[0].id
		error_message="The Monitoring Metrics Publisher grant must be scoped to the Prometheus data collection rule"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_not_create_acr_pull_role_by_default" {
	assert {
		condition=length(azurerm_role_assignment.acr_pull) == 0
		error_message="Without container registries no AcrPull role assignment must be created"
	}
	command=plan
}
run "should_not_create_observability_identity_by_default" {
	assert {
		condition=length(azurerm_user_assigned_identity.observability) == 0 && length(azurerm_federated_identity_credential.observability) == 0 && length(azurerm_federated_identity_credential.observability_alloy) == 0
		error_message="No observability identity or federated credential must be created when observability is disabled"
	}
	assert {
		condition=length(azurerm_role_assignment.observability_monitoring_data_reader) == 0 && length(azurerm_role_assignment.observability_monitoring_metrics_publisher) == 0
		error_message="No monitoring grant must be created when observability is disabled"
	}
	assert {
		condition=output.observability_identity_client_id == ""
		error_message="The observability identity client ID output must be empty when observability is disabled"
	}
	command=plan
}
run "should_not_create_workload_storage_role" {
	assert {
		condition=length(azurerm_role_assignment.workload_storage) == 0
		error_message="Without storage scopes no workload storage role assignment must be created"
	}
	command=plan
}
run "should_override_observability_namespace" {
	assert {
		condition=azurerm_federated_identity_credential.observability[0].subject == "system:serviceaccount:metrics:grafana"
		error_message="A custom observability namespace must drive the federated credential subject"
	}
	assert {
		condition=azurerm_federated_identity_credential.observability_alloy[0].subject == "system:serviceaccount:metrics:liferay-alloy"
		error_message="A custom observability namespace must drive the alloy federated credential subject"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
			namespace="metrics"
		}
	}
}
variables {
	deployment_name="liferay-test"
	region="eastus"
}