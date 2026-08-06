mock_provider "azurerm" {
	mock_data "azurerm_key_vault" {
		defaults={
			id="/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/liferay-test-vault/providers/Microsoft.KeyVault/vaults/liferay-test-vault"
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
override_module {
	target=module.argocd
}
run "should_assemble_the_cluster_identity" {
	assert {
		condition=join(",", keys(local.cluster_identity)) == "crossplaneDataClientId,crossplaneIamClientId,deploymentName,oidcIssuerUrl,region,resourceGroupName,subscriptionId,tenantId"
		error_message="The cluster identity must carry exactly the keys the infrastructure provider consumes"
	}
	assert {
		condition=local.cluster_identity.deploymentName == "liferay-test"
		error_message="The cluster identity must carry the deployment name"
	}
	assert {
		condition=local.cluster_identity.region == "eastus"
		error_message="The cluster identity must carry the deployment region"
	}
	command=plan
}
run "should_default_to_the_azure_key_vault_secret_store" {
	assert {
		condition=join(",", keys(local.cluster_secret_store_provider)) == "azurekv"
		error_message="The secret store provider must default to Azure Key Vault"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.authType == "WorkloadIdentity"
		error_message="The default Azure Key Vault provider must authenticate through workload identity"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.serviceAccountRef.name == "external-secrets" && local.cluster_secret_store_provider.azurekv.serviceAccountRef.namespace == "external-secrets-system"
		error_message="The default Azure Key Vault provider must reference the External Secrets service account"
	}
	assert {
		condition=local.cluster_secret_store_provider.azurekv.vaultUrl == data.azurerm_key_vault.liferay[0].vault_uri
		error_message="The default Azure Key Vault provider must point at the deployment vault"
	}
	command=plan
}
run "should_derive_azure_resource_names_from_the_deployment_name" {
	assert {
		condition=data.azurerm_kubernetes_cluster.aks.name == "liferay-test-aks"
		error_message="The AKS cluster lookup must derive its name from the deployment name"
	}
	assert {
		condition=data.azurerm_key_vault.liferay[0].name == "liferay-test-vault"
		error_message="The Key Vault lookup must derive its name from the deployment name with the -vault suffix"
	}
	assert {
		condition=data.azurerm_key_vault.liferay[0].resource_group_name == "liferay-test-vault"
		error_message="The Key Vault lookup must target the dedicated vault resource group named after the vault"
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
		cluster_secret_store_provider_hcl={
			vault={
				server="https://vault.example.com:8200"
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
		key_vault_name="custom-vault"
		key_vault_resource_group_name="custom-group"
	}
}
run "should_reject_a_deployment_name_the_vault_name_cannot_absorb" {
	command=plan
	expect_failures=[
		var.deployment_name,
	]
	variables {
		deployment_name="liferay-test-overflowing"
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
		condition=local.cluster_identity.crossplaneDataClientId == azurerm_user_assigned_identity.crossplane_data.client_id
		error_message="The cluster identity must carry the Crossplane data identity client ID"
	}
	assert {
		condition=local.cluster_identity.crossplaneIamClientId == azurerm_user_assigned_identity.crossplane_iam.client_id
		error_message="The cluster identity must carry the Crossplane IAM identity client ID"
	}
	assert {
		condition=output.external_secrets_client_id == azurerm_user_assigned_identity.external_secrets.client_id
		error_message="The External Secrets client ID must be published so the bootstrap can annotate the service account"
	}
	assert {
		condition=output.cluster_identity == local.cluster_identity
		error_message="The cluster identity must be published for the bootstrap to place under clusterIdentity"
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
		condition=data.azurerm_role_definition.key_vault_crypto_service_encryption_user.name == "Key Vault Crypto Service Encryption User" && data.azurerm_role_definition.storage_blob_data_contributor.name == "Storage Blob Data Contributor"
		error_message="The grantable role allowlist must resolve the intended built in roles by name"
	}
	assert {
		condition=strcontains(azurerm_role_assignment.crossplane_iam_rbac_administrator.condition, basename(data.azurerm_role_definition.key_vault_crypto_service_encryption_user.role_definition_id)) && strcontains(azurerm_role_assignment.crossplane_iam_rbac_administrator.condition, basename(data.azurerm_role_definition.storage_blob_data_contributor.role_definition_id))
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
	deployment_name="liferay-test"
	region="eastus"
}