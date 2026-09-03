mock_provider "azurerm" {
	mock_data "azurerm_key_vault" {
		defaults={
			id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test-vault/providers/Microsoft.KeyVault/vaults/liferay-test-vault"
			rbac_authorization_enabled=true
		}
	}
	mock_data "azurerm_resource_group" {
		defaults={
			id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test"
		}
	}
	mock_resource "azurerm_user_assigned_identity" {
		defaults={
			id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.ManagedIdentity/userAssignedIdentities/liferay-test-identity"
		}
	}
}
mock_provider "kubernetes" {}
override_module {
	target=module.argocd
}
override_resource {
	override_during=plan
	target=azurerm_monitor_data_collection_endpoint.main[0]
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Insights/dataCollectionEndpoints/liferay-test-prometheus-dce"
		metrics_ingestion_endpoint="https://liferay-test-prometheus-dce-abcd.eastus-1.metrics.ingest.monitor.azure.com"
	}
}
override_resource {
	override_during=plan
	target=azurerm_monitor_data_collection_rule.main[0]
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Insights/dataCollectionRules/liferay-test-prometheus-dcr"
		immutable_id="dcr-00000000000000000000000000000000"
	}
}
override_resource {
	override_during=plan
	target=azurerm_monitor_workspace.main[0]
	values={
		id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test/providers/Microsoft.Monitor/accounts/liferay-test-amw"
	}
}
run "should_align_the_karpenter_node_pool_with_the_system_machine_type" {
	assert {
		condition=one([for requirement in kubernetes_manifest.karpenter_node_pool.manifest.spec.template.spec.requirements : requirement.values if requirement.key == "karpenter.azure.com/sku-name"]) == ["Standard_D16s_v3"]
		error_message="The Karpenter node pool must use the specified machine type"
	}
	assert {
		condition=kubernetes_manifest.karpenter_node_pool.manifest.spec.weight > 0
		error_message="The Karpenter node pool must have high priority"
	}
	assert {
		condition=try(kubernetes_manifest.karpenter_node_pool.manifest.spec.disruption.consolidateAfter, "") != ""
		error_message="The Karpenter node pool must set a consolidateAfter"
	}
	assert {
		condition=kubernetes_manifest.karpenter_node_pool.manifest.spec.template.spec.nodeClassRef.name == kubernetes_manifest.karpenter_node_class.manifest.metadata.name
		error_message="The Karpenter node pool must reference the node class this module declares"
	}
	command=plan
	override_data {
		target=data.azurerm_kubernetes_cluster.aks
		values={
			agent_pool_profile=[
				{
					name="system"
					vm_size="Standard_D16s_v3"
				},
			]
		}
	}
}
run "should_assemble_the_deployment_context" {
	assert {
		condition=join(",", keys(local.deployment_context)) == "crossplaneDataClientId,crossplaneIamClientId,deploymentName,oidcIssuerUrl,region,resourceGroupName,subscriptionId,tenantId"
		error_message="The deployment context must carry exactly the keys the infrastructure provider consumes"
	}
	assert {
		condition=local.deployment_context.deploymentName == "liferay-test"
		error_message="The deployment context must carry the deployment name"
	}
	assert {
		condition=local.deployment_context.region == "eastus"
		error_message="The deployment context must carry the deployment region"
	}
	command=plan
}
run "should_bind_the_keda_identity_to_the_monitor_workspace" {
	assert {
		condition=azurerm_user_assigned_identity.keda[0].name == "liferay-test-keda"
		error_message="The KEDA identity name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_federated_identity_credential.keda[0].name == "keda"
		error_message="The KEDA federated credential must not include a deployment name prefix"
	}
	assert {
		condition=azurerm_federated_identity_credential.keda[0].subject == "system:serviceaccount:keda-system:keda-operator"
		error_message="The KEDA federated credential must bind the keda-operator service account in the KEDA namespace"
	}
	assert {
		condition=one(azurerm_federated_identity_credential.keda[0].audience) == "api://AzureADTokenExchange"
		error_message="The KEDA federated credential must use the Azure AD token exchange audience"
	}
	assert {
		condition=azurerm_role_assignment.keda_monitoring_data_reader[0].role_definition_name == "Monitoring Data Reader"
		error_message="The KEDA identity must be granted the Monitoring Data Reader role"
	}
	assert {
		condition=azurerm_role_assignment.keda_monitoring_data_reader[0].scope == azurerm_monitor_workspace.main[0].id
		error_message="The KEDA Monitoring Data Reader grant must be scoped to the Azure Monitor workspace"
	}
	assert {
		condition=output.keda_service_account_namespace == "keda-system"
		error_message="The KEDA service account namespace must be published"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
		}
		observability_config={
			enabled=true
		}
	}
}
run "should_build_the_azure_key_vault_secret_store_provider" {
	assert {
		condition=join(",", keys(local.cluster_secret_store_provider)) == "azurekv"
		error_message="The key vault branch must produce an Azure Key Vault provider"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.authType == "WorkloadIdentity"
		error_message="The Azure Key Vault provider must authenticate through workload identity"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.serviceAccountRef.name == "external-secrets" && local.cluster_secret_store_provider.azurekv.serviceAccountRef.namespace == "external-secrets-system"
		error_message="The Azure Key Vault provider must reference the External Secrets service account"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.tenantId == data.azurerm_client_config.current.tenant_id
		error_message="The Azure Key Vault provider must carry the tenant ID required by workload identity"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.vaultUrl == data.azurerm_key_vault.liferay[0].vault_uri
		error_message="The Azure Key Vault provider must point at the configured vault"
	}
	command=plan
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
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_create_observability_identity_when_enabled" {
	assert {
		condition=azurerm_user_assigned_identity.observability[0].name == "liferay-test-observability"
		error_message="The observability identity name must be derived from deployment_name"
	}
	assert {
		condition=azurerm_federated_identity_credential.observability[0].name == "grafana"
		error_message="The observability federated credential must take the bare purpose name without a deployment name prefix"
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
		condition=azurerm_federated_identity_credential.observability_alloy[0].name == "alloy"
		error_message="The alloy federated credential must take the bare purpose name without a deployment name prefix"
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
run "should_derive_azure_resource_names_from_the_deployment_name" {
	assert {
		condition=data.azurerm_kubernetes_cluster.aks.name == "liferay-test-aks"
		error_message="The AKS cluster lookup must derive its name from the deployment name"
	}
	assert {
		condition=azurerm_user_assigned_identity.crossplane_data.name == "liferay-test-crossplane-data"
		error_message="The Crossplane data identity must derive its name from the deployment name"
	}
	assert {
		condition=azurerm_user_assigned_identity.crossplane_iam.name == "liferay-test-crossplane-iam"
		error_message="The Crossplane IAM identity must derive its name from the deployment name"
	}
	assert {
		condition=azurerm_user_assigned_identity.external_secrets.name == "liferay-test-external-secrets"
		error_message="The External Secrets identity must derive its name from the deployment name"
	}
	command=plan
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
		condition=output.prometheus_data_collection_rule_id == "" && output.prometheus_metrics_ingestion_endpoint == ""
		error_message="Remote write outputs must be empty when observability is disabled"
	}
	command=plan
}
run "should_expose_remote_write_outputs_when_enabled" {
	assert {
		condition=output.prometheus_metrics_ingestion_endpoint == "https://liferay-test-prometheus-dce-abcd.eastus-1.metrics.ingest.monitor.azure.com"
		error_message="The metrics ingestion endpoint output must expose the data collection endpoint that receives remote write requests"
	}
	assert {
		condition=output.prometheus_data_collection_rule_id == "dcr-00000000000000000000000000000000"
		error_message="The data collection rule output must expose the immutable ID that the remote write URL embeds"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
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
run "should_honor_a_custom_keda_service_account" {
	assert {
		condition=azurerm_federated_identity_credential.keda[0].subject == "system:serviceaccount:keda:keda-operator-custom"
		error_message="The KEDA federated credential subject must follow the configured namespace and service account name"
	}
	assert {
		condition=output.keda_service_account_namespace == "keda"
		error_message="The published KEDA namespace must follow the configured namespace"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
			namespace="keda"
			service_account_name="keda-operator-custom"
		}
		observability_config={
			enabled=true
		}
	}
}
run "should_inject_an_external_secret_store_provider" {
	assert {
		condition=local.cluster_secret_store_provider.vault.server == "https://vault.example.com:8200"
		error_message="A configured external secret store provider must flow to the platform module verbatim"
	}
	assert {
		condition=!contains(keys(local.cluster_secret_store_provider), "azurekv")
		error_message="An external secret store provider must replace the default Azure Key Vault provider"
	}
	assert {
		condition=length(data.azurerm_key_vault.liferay) == 0
		error_message="An external secret store provider must skip the Key Vault lookup"
	}
	assert {
		condition=length(azurerm_role_assignment.external_secrets_vault) == 0
		error_message="An external secret store provider must skip the Key Vault role assignment"
	}
	command=plan
	variables {
		cluster_secret_store={
			provider_hcl={
				vault={
					server="https://vault.example.com:8200"
				}
			}
		}
	}
}
run "should_look_up_the_key_vault_from_the_configured_names" {
	assert {
		condition=data.azurerm_key_vault.liferay[0].name == "custom-vault"
		error_message="The Key Vault lookup must use the configured key vault name"
	}
	assert {
		condition=data.azurerm_key_vault.liferay[0].resource_group_name == "custom-group"
		error_message="The Key Vault lookup must use the configured key vault resource group name"
	}
	command=plan
	variables {
		cluster_secret_store={
			key_vault={
				name="custom-vault"
				resource_group_name="custom-group"
			}
		}
	}
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
run "should_omit_the_keda_identity_by_default" {
	assert {
		condition=length(azurerm_user_assigned_identity.keda) == 0
		error_message="KEDA identity should not be created by default"
	}
	assert {
		condition=length(azurerm_federated_identity_credential.keda) == 0
		error_message="KEDA federated credential should not be created by default"
	}
	assert {
		condition=length(azurerm_role_assignment.keda_monitoring_data_reader) == 0
		error_message="Monitoring Data Reader must not be granted by default to KEDA"
	}
	assert {
		condition=output.keda_identity_client_id == "" && output.keda_service_account_namespace == ""
		error_message="The KEDA outputs must be empty so the bootstrap leaves KEDA unconfigured"
	}
	command=plan
}
run "should_omit_the_keda_identity_when_observability_is_disabled" {
	assert {
		condition=length(azurerm_user_assigned_identity.keda) == 0
		error_message="Enabling KEDA without observability must not create the KEDA identity"
	}
	assert {
		condition=length(azurerm_role_assignment.keda_monitoring_data_reader) == 0
		error_message="Enabling KEDA without observability must not grant Monitoring Data Reader"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
		}
	}
}
run "should_reject_a_cluster_secret_store_with_both_branches" {
	command=plan
	expect_failures=[
		var.cluster_secret_store,
	]
	variables {
		cluster_secret_store={
			key_vault={
				name="liferay-test-vault"
				resource_group_name="liferay-test-vault"
			}
			provider_hcl={
				vault={
					server="https://vault.example.com:8200"
				}
			}
		}
	}
}
run "should_reject_a_key_vault_without_rbac_authorization" {
	command=plan
	expect_failures=[
		data.azurerm_key_vault.liferay,
	]
	override_data {
		target=data.azurerm_key_vault.liferay
		values={
			rbac_authorization_enabled=false
		}
	}
}
run "should_reject_an_empty_cluster_secret_store" {
	command=plan
	expect_failures=[
		var.cluster_secret_store,
	]
	variables {
		cluster_secret_store={}
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
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_wire_the_platform_identities" {
	assert {
		condition=azurerm_federated_identity_credential.crossplane_data.subject == "system:serviceaccount:crossplane-system:crossplane-data"
		error_message="The Crossplane data federated credential must pin the provider service account subject"
	}
	assert {
		condition=azurerm_federated_identity_credential.crossplane_iam.subject == "system:serviceaccount:crossplane-system:crossplane-iam"
		error_message="The Crossplane IAM federated credential must pin the provider service account subject"
	}
	assert {
		condition=azurerm_federated_identity_credential.external_secrets.subject == "system:serviceaccount:external-secrets-system:external-secrets"
		error_message="The External Secrets federated credential subject must match the secret store service account reference"
	}
	assert {
		condition=azurerm_federated_identity_credential.external_secrets.issuer == data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
		error_message="The federated credentials must trust the cluster OIDC issuer"
	}
	assert {
		condition=local.deployment_context.crossplaneDataClientId == azurerm_user_assigned_identity.crossplane_data.client_id
		error_message="The deployment context must carry the Crossplane data identity client ID"
	}
	assert {
		condition=local.deployment_context.crossplaneIamClientId == azurerm_user_assigned_identity.crossplane_iam.client_id
		error_message="The deployment context must carry the Crossplane IAM identity client ID"
	}
	assert {
		condition=output.external_secrets_client_id == azurerm_user_assigned_identity.external_secrets.client_id
		error_message="The External Secrets client ID must be published so the bootstrap can annotate the service account"
	}
	assert {
		condition=output.deployment_context == local.deployment_context
		error_message="The deployment context must be published for the bootstrap to place under deploymentContext"
	}
	assert {
		condition=output.cluster_secret_store_provider == local.cluster_secret_store_provider
		error_message="The secret store provider must be published for the bootstrap to place under clusterSecretStore.provider"
	}
	assert {
		condition=azurerm_role_assignment.crossplane_data_contributor.role_definition_name == "Contributor"
		error_message="The Crossplane data identity must hold Contributor on the resource group"
	}
	assert {
		condition=azurerm_role_assignment.crossplane_iam_managed_identity_contributor.role_definition_name == "Managed Identity Contributor"
		error_message="The Crossplane IAM identity must hold Managed Identity Contributor on the resource group"
	}
	assert {
		condition=azurerm_role_assignment.crossplane_iam_rbac_administrator.role_definition_name == "Role Based Access Control Administrator"
		error_message="The Crossplane IAM identity must hold Role Based Access Control Administrator on the resource group"
	}
	assert {
		condition=join(",", [for role_definition in [data.azurerm_role_definition.backup_operator, data.azurerm_role_definition.key_vault_crypto_service_encryption_user, data.azurerm_role_definition.storage_account_backup_contributor, data.azurerm_role_definition.storage_blob_data_contributor, data.azurerm_role_definition.storage_blob_data_reader] : role_definition.name]) == "Backup Operator,Key Vault Crypto Service Encryption User,Storage Account Backup Contributor,Storage Blob Data Contributor,Storage Blob Data Reader"
		error_message="The grantable role allowlist must resolve the intended built in roles by name"
	}
	assert {
		condition=length(local.crossplane_iam_grantable_role_definition_ids) == 5 && alltrue([for role_definition in [data.azurerm_role_definition.backup_operator, data.azurerm_role_definition.key_vault_crypto_service_encryption_user, data.azurerm_role_definition.storage_account_backup_contributor, data.azurerm_role_definition.storage_blob_data_contributor, data.azurerm_role_definition.storage_blob_data_reader] : strcontains(azurerm_role_assignment.crossplane_iam_rbac_administrator.condition, basename(role_definition.role_definition_id))])
		error_message="The role assignment condition must restrict grantable roles to the allowlist"
	}
	assert {
		condition=strcontains(azurerm_role_assignment.crossplane_iam_rbac_administrator.condition, "@Request[Microsoft.Authorization/roleAssignments:RoleDefinitionId]") && strcontains(azurerm_role_assignment.crossplane_iam_rbac_administrator.condition, "@Resource[Microsoft.Authorization/roleAssignments:RoleDefinitionId]")
		error_message="The condition must constrain the write and delete actions through their respective attribute sources"
	}
	assert {
		condition=strcontains(azurerm_role_assignment.crossplane_iam_rbac_administrator.condition, "'ServicePrincipal'")
		error_message="The condition must restrict grants to service principals"
	}
	assert {
		condition=length(azurerm_role_assignment.external_secrets_vault) == 1 && azurerm_role_assignment.external_secrets_vault[0].role_definition_name == "Key Vault Secrets User"
		error_message="The External Secrets identity must hold Key Vault Secrets User on the deployment vault"
	}
	command=apply
}
variables {
	argocd_helm_chart_version="10.1.3"
	cluster_secret_store={
		key_vault={
			name="liferay-test-vault"
			resource_group_name="liferay-test-vault"
		}
	}
	deployment_name="liferay-test"
	region="eastus"
}