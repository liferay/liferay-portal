mock_provider "azurerm" {
	source="./tests/mocks"
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
run "should_not_create_workload_storage_role" {
	assert {
		condition=length(azurerm_role_assignment.workload_storage) == 0
		error_message="Without storage scopes no workload storage role assignment must be created"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	region="eastus"
}