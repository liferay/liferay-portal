mock_provider "azurerm" {
	source="./tests/mocks"
}
run "should_create_cluster_identity" {
	assert {
		condition=azurerm_user_assigned_identity.cluster.name == "liferay-test-cluster-identity"
		error_message="The cluster identity name must be derived from deployment_name"
	}
	command=plan
}
run "should_grant_cluster_network_contributor" {
	assert {
		condition=azurerm_role_assignment.cluster_network_contributor.role_definition_name == "Network Contributor"
		error_message="The cluster identity must be granted the Network Contributor role on the virtual network"
	}
	command=plan
}
run "should_not_create_acr_pull_role_by_default" {
	assert {
		condition=length(azurerm_role_assignment.acr_pull) == 0
		error_message="Without container registries no AcrPull role assignment must be created"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	region="eastus"
}