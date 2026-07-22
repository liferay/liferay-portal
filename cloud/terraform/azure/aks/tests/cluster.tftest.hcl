mock_provider "azurerm" {
	source="./tests/mocks"
}
run "should_apply_default_node_pool_settings" {
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].auto_scaling_enabled == false
		error_message="The default node pool must not enable cluster autoscaling"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].host_encryption_enabled == false
		error_message="The default node pool must not encrypt temp disks and caches at the host"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].max_pods == 50
		error_message="The default node pool must allow up to 50 pods per node"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].name == "system"
		error_message="The default node pool must be named \"system\""
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].node_count == 2
		error_message="The default node pool must default to 2 nodes"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].only_critical_addons_enabled == true
		error_message="The default node pool must only schedule critical addons"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].os_disk_type == "Ephemeral"
		error_message="The default node pool must use ephemeral OS disks"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].vm_size == "Standard_D4ds_v4"
		error_message="The default node pool must default to the Standard_D4ds_v4 VM size"
	}
	command=plan
}
run "should_create_api_server_access_profile_when_ip_ranges_set" {
	assert {
		condition=length(azurerm_kubernetes_cluster.main.api_server_access_profile) == 1
		error_message="A public cluster with authorized IP ranges must create an api_server_access_profile block"
	}
	assert {
		condition=one(azurerm_kubernetes_cluster.main.api_server_access_profile[0].authorized_ip_ranges) == "1.2.3.4/32"
		error_message="The api_server_access_profile must expose the configured authorized IP ranges"
	}
	command=plan
	variables {
		api_authorized_ip_ranges=["1.2.3.4/32"]
	}
}
run "should_default_to_public_cluster" {
	assert {
		condition=azurerm_kubernetes_cluster.main.private_cluster_enabled == false
		error_message="The cluster must be public by default"
	}
	command=plan
}
run "should_derive_cluster_name_from_deployment_name" {
	assert {
		condition=local.cluster_name == "liferay-test-aks"
		error_message="The local cluster_name variable must be \"<deployment_name>-aks\""
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.dns_prefix == "liferay-test"
		error_message="The cluster dns_prefix must be deployment_name"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.name == "liferay-test-aks"
		error_message="The AKS cluster name must be derived from deployment_name"
	}
	command=plan
}
run "should_enable_private_cluster" {
	assert {
		condition=azurerm_kubernetes_cluster.main.private_cluster_enabled == true
		error_message="Setting private_cluster must enable a private cluster"
	}
	command=plan
	variables {
		private_cluster=true
	}
}
run "should_expose_inputs_as_outputs" {
	assert {
		condition=output.cluster_name == "liferay-test-aks"
		error_message="The cluster_name output must expose the derived cluster name"
	}
	assert {
		condition=output.deployment_name == "liferay-test"
		error_message="The deployment_name output must echo var.deployment_name"
	}
	assert {
		condition=output.region == "eastus"
		error_message="The region output must echo var.region"
	}
	assert {
		condition=output.resource_group_name == "liferay-test"
		error_message="The resource_group_name output must be deployment_name"
	}
	command=plan
}
run "should_harden_cluster_defaults" {
	assert {
		condition=azurerm_kubernetes_cluster.main.automatic_upgrade_channel == "stable"
		error_message="The cluster must follow the stable automatic upgrade channel"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.azure_policy_enabled == true
		error_message="Azure Policy must be enabled"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.image_cleaner_enabled == true
		error_message="The image cleaner must be enabled"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.image_cleaner_interval_hours == 48
		error_message="The image cleaner must run on a 48 hour interval"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.key_vault_secrets_provider[0].secret_rotation_enabled == true
		error_message="The Secrets Store CSI Driver must autorotate secrets"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.local_account_disabled == false
		error_message="Local accounts must remain enabled"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.node_os_upgrade_channel == "NodeImage"
		error_message="The node OS upgrade channel must be NodeImage"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.oidc_issuer_enabled == true
		error_message="The OIDC issuer must be enabled"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.sku_tier == "Standard"
		error_message="The cluster must use the Standard SKU tier"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.workload_identity_enabled == true
		error_message="Workload identity must be enabled"
	}
	command=plan
}
run "should_not_create_api_server_access_profile_by_default" {
	assert {
		condition=length(azurerm_kubernetes_cluster.main.api_server_access_profile) == 0
		error_message="Without authorized IP ranges no api_server_access_profile block must be created"
	}
	command=plan
}
run "should_not_create_api_server_access_profile_for_private_cluster" {
	assert {
		condition=length(azurerm_kubernetes_cluster.main.api_server_access_profile) == 0
		error_message="A private cluster must not create an api_server_access_profile block even with authorized IP ranges"
	}
	command=plan
	variables {
		api_authorized_ip_ranges=["1.2.3.4/32"]
		private_cluster=true
	}
}
run "should_override_machine_type" {
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].vm_size == "Standard_D8ds_v4"
		error_message="A custom machine_type must drive the default node pool VM size"
	}
	command=plan
	variables {
		machine_type="Standard_D8ds_v4"
	}
}
run "should_override_system_node_count" {
	assert {
		condition=azurerm_kubernetes_cluster.main.default_node_pool[0].node_count == 5
		error_message="A custom system_node_count must drive the default node pool node count"
	}
	command=plan
	variables {
		system_node_count=5
	}
}
run "should_tag_resources_with_deployment_and_region" {
	assert {
		condition=azurerm_kubernetes_cluster.main.tags["deployment"] == "liferay-test"
		error_message="Resources must be tagged with the deployment name"
	}
	assert {
		condition=azurerm_kubernetes_cluster.main.tags["region"] == "eastus"
		error_message="Resources must be tagged with the region"
	}
	command=plan
}
run "should_use_user_assigned_identity" {
	assert {
		condition=azurerm_kubernetes_cluster.main.identity[0].type == "UserAssigned"
		error_message="The cluster must use a user-assigned managed identity"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	region="eastus"
}