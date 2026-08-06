locals {
	cluster_identity={
		crossplaneDataClientId=azurerm_user_assigned_identity.crossplane_data.client_id
		crossplaneIamClientId=azurerm_user_assigned_identity.crossplane_iam.client_id
		deploymentName=var.deployment_name
		oidcIssuerUrl=data.azurerm_kubernetes_cluster.aks.oidc_issuer_url
		region=var.region
		resourceGroupName=local.resource_group_name
		subscriptionId=data.azurerm_client_config.current.subscription_id
		tenantId=data.azurerm_client_config.current.tenant_id
	}
	cluster_secret_store_provider=local.default_azure_key_vault_enabled ? {
		azurekv={
			authType="WorkloadIdentity"
			serviceAccountRef=local.external_secrets_service_account
			vaultUrl=data.azurerm_key_vault.liferay[0].vault_uri
		}
	} : var.cluster_secret_store_provider_hcl
	crossplane_iam_grantable_role_definition_ids=[
		basename(data.azurerm_role_definition.key_vault_crypto_service_encryption_user.role_definition_id),
		basename(data.azurerm_role_definition.storage_blob_data_contributor.role_definition_id),
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
	default_azure_key_vault_enabled=var.cluster_secret_store_provider_hcl == null
	external_secrets_service_account={
		name="external-secrets"
		namespace="external-secrets-system"
	}
	key_vault_name=coalesce(var.key_vault_name, "${var.deployment_name}-vault")
	key_vault_resource_group_name=coalesce(var.key_vault_resource_group_name, "${var.deployment_name}-vault")
	resource_group_name=var.deployment_name
}