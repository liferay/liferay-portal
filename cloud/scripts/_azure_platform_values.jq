include "_platform_values" {search: "./"};

$configuration[0] as $configuration
| . as $outputs
| $outputs.deployment_context.value as $deployment_context
| $outputs.observability_identity_client_id.value as $observability_identity_client_id
| $outputs.prometheus_workspace_endpoint.value as $prometheus_workspace_endpoint
| ($outputs.prometheus_workspace_id.value // "") as $prometheus_workspace_id
| {
	platformComponents: {
		values: {
			clusterSecretStore: {
				enabled: true,
				name: ($deployment_context.deploymentName + "-secret-store"),
				provider: $outputs.cluster_secret_store_provider.value
			},
			deploymentContext: $deployment_context,
			infrastructure: {
				parameters: [
					{
						name: "persistence.storageClassName",
						value: "managed-csi-premium-v2"
					}
				]
			},
			liferay: (
				if $prometheus_workspace_id == "" then
					{}
				else
					{
						parameters: [
							{
								name: "global.azure.prometheusWorkspaceEndpoint",
								value: $prometheus_workspace_endpoint
							}
						]
					}
				end),
			networkPolicy: {
				apiServerSources: [
					{
						namespaceSelector: {
							matchLabels: {
								"kubernetes.io/metadata.name": "kube-system"
							}
						},
						podSelector: {
							matchLabels: {
								app: "konnectivity-agent"
							}
						}
					}
				]
			},
			observability: (
				if $prometheus_workspace_id == "" then
					{}
				else
					{
						enabled: true,
						parameters: [
							{
								name: "alloy.iam.azureClientId",
								value: $observability_identity_client_id
							},
							{
								name: "azure.location",
								value: $deployment_context.region
							},
							{
								name: "azure.prometheusWorkspaceEndpoint",
								value: $prometheus_workspace_endpoint
							},
							{
								name: "azure.prometheusWorkspaceId",
								value: $prometheus_workspace_id
							},
							{
								name: "azure.remoteWrite.dataCollectionRuleId",
								value: $outputs.prometheus_data_collection_rule_id.value
							},
							{
								name: "azure.remoteWrite.metricsIngestionEndpoint",
								value: $outputs.prometheus_metrics_ingestion_endpoint.value
							},
							{
								name: "azure.remoteWrite.tenantId",
								value: $deployment_context.tenantId
							},
							{
								name: "azure.resourceGroupName",
								value: $deployment_context.resourceGroupName
							},
							{
								name: "azure.subscriptionId",
								value: $deployment_context.subscriptionId
							},
							{
								name: "cloudProvider",
								value: "azure"
							},
							{
								name: "grafana.grafana\\.ini.azure.workload_identity_client_id",
								value: $observability_identity_client_id
							},
							{
								name: "grafana.grafana\\.ini.azure.workload_identity_tenant_id",
								value: $deployment_context.tenantId
							},
							{
								name: "grafana.serviceAccount.annotations.azure\\.workload\\.identity/client-id",
								value: $observability_identity_client_id
							}
						]
					}
				end),
			operatorApplications: {
				externalSecrets: {
					values: {
						serviceAccount: {
							annotations: {
								"azure.workload.identity/client-id": $outputs.external_secrets_client_id.value
							}
						}
					}
				},
				keda: (
					($outputs.keda_identity_client_id.value // "") as $client_id
					| if $client_id == "" then
						{}
					else
						{
							enabled: true,
							namespace: ($outputs.keda_service_account_namespace.value // "keda-system"),
							values: {
								podIdentity: {
									azureWorkload: {
										clientId: $client_id,
										enabled: true,
										tenantId: $deployment_context.tenantId
									}
								}
							}
						}
					end)
			}
		}
	}
}
| platform_values($configuration; "liferay-azure-infrastructure-provider"; "liferay-azure")
* {
	platformComponents: {
		values: {
			database: {
				engine: $configuration.setup.database.engine
			}
		}
	}
}