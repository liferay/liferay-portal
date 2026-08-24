locals {
	cluster_secret_store_provider=local.default_azure_key_vault_enabled ? {
		azurekv={
			authType="WorkloadIdentity"
			serviceAccountRef=local.external_secrets_service_account
			tenantId=data.azurerm_client_config.current.tenant_id
			vaultUrl=data.azurerm_key_vault.liferay[0].vault_uri
		}
	} : var.cluster_secret_store.provider_hcl
	crossplane_iam_grantable_role_definition_ids=[
		basename(data.azurerm_role_definition.key_vault_crypto_service_encryption_user.role_definition_id),
		basename(data.azurerm_role_definition.storage_blob_data_contributor.role_definition_id),
		basename(data.azurerm_role_definition.storage_blob_data_reader.role_definition_id),
	]
	crossplane_iam_role_assignment_condition=join(" AND ", [
		for clause in [
			{
				action="delete"
				source="@Resource"
			},
			{
				action="write"
				source="@Request"
			},
		] : trimspace(replace(<<-EOT
			(
				(!(ActionMatches{'Microsoft.Authorization/roleAssignments/${clause.action}'}))
				OR
				(
					${clause.source}[Microsoft.Authorization/roleAssignments:RoleDefinitionId] ForAnyOfAnyValues:GuidEquals {${join(", ", local.crossplane_iam_grantable_role_definition_ids)}}
					AND
					${clause.source}[Microsoft.Authorization/roleAssignments:PrincipalType] ForAnyOfAnyValues:StringEqualsIgnoreCase {'ServicePrincipal'}
				)
			)
		EOT
		, "\t", " "))
	])
	default_azure_key_vault_enabled=var.cluster_secret_store.key_vault != null
	deployment_context={
		crossplaneDataClientId=azurerm_user_assigned_identity.crossplane_data.client_id
		crossplaneIamClientId=azurerm_user_assigned_identity.crossplane_iam.client_id
		deploymentName=var.deployment_name
		oidcIssuerUrl=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
		region=var.region
		resourceGroupName=local.resource_group_name
		subscriptionId=data.azurerm_client_config.current.subscription_id
		tenantId=data.azurerm_client_config.current.tenant_id
	}
	external_secrets_service_account={
		name="external-secrets"
		namespace="external-secrets-system"
	}
	keda_enabled=var.keda_config.enabled && var.observability_config.enabled
	resource_group_name=var.deployment_name
	system_node_pool_vm_size=one([
		for agent_pool_profile in data.azurerm_kubernetes_cluster.aks.agent_pool_profile :
		agent_pool_profile.vm_size
		if agent_pool_profile.name == "system"
	])
	tags={
		deployment=var.deployment_name
		region=var.region
	}
}