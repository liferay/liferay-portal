include "_platform_values" {search: "./"};

$configuration[0] as $configuration
| . as $outputs
| $outputs.deployment_context.value as $deployment_context
| ($outputs.keda_service_account_namespace.value // "") as $keda_service_account_namespace
| $outputs.marketplace_filestore_ip.value as $marketplace_filestore_ip
| $outputs.marketplace_volume_handle.value as $marketplace_volume_handle
| ($configuration.terraform.platform.observability_config.enabled // false) as $observability_enabled
| {
	"networking.gke.io/load-balancer-type": "External"
} as $operator_service_annotations
| "https://monitoring.googleapis.com/v1/projects/\($deployment_context.projectId)/location/global/prometheus" as $prometheus_query_endpoint
| {
	platformComponents: {
		values: {
			argoWorkflows: {
				gateway: {
					envoyProxy: {
						serviceAnnotations: $operator_service_annotations
					}
				}
			},
			argocd: {
				gateway: {
					envoyProxy: {
						serviceAnnotations: $operator_service_annotations
					}
				}
			},
			clusterSecretStore: {
				enabled: true,
				name: ($deployment_context.deploymentName + "-secret-store"),
				provider: $outputs.cluster_secret_store_provider.value
			},
			deploymentContext: $deployment_context,
			infrastructure: {
				parameters: [
					{
						name: "databaseAppPassword.enabled",
						value: "false"
					},
					{
						name: "persistence.storageClassName",
						value: "standard-rwo"
					}
				]
			},
			liferay: {
				parameters: [
					{
						name: "liferay-default.marketplace.csi.volumeAttributes.ip",
						value: $marketplace_filestore_ip
					},
					{
						name: "liferay-default.marketplace.csi.volumeHandle",
						value: $marketplace_volume_handle
					}
				]
				+ (
					if $keda_service_account_namespace == "" then
						[]
					else
						[
							{
								name: "liferay-default.autoscaling.keda.prometheusTriggerMetadata.serverAddress",
								value: $prometheus_query_endpoint
							}
						]
					end)
			},
			networkPolicy: {
				apiServerSources: [
					(
						$outputs.kubernetes_endpoint_cidrs.value[]
						| {
							ipBlock: {
								cidr: .
							}
						}
					),
					{
						namespaceSelector: {
							matchLabels: {
								"kubernetes.io/metadata.name": "kube-system"
							}
						},
						podSelector: {
							matchLabels: {
								"k8s-app": "konnectivity-agent"
							}
						}
					}
				],
				metricsSources: [
					{
						namespaceSelector: {
							matchLabels: {
								"kubernetes.io/metadata.name": "gmp-system"
							}
						}
					}
				]
			},
			observability: (
				if $observability_enabled | not then
					{}
				else
					{
						enabled: true,
						parameters: [
							{
								name: "cloudProvider",
								value: "gcp"
							},
							{
								name: "gcp.clusterName",
								value: $cluster_name
							},
							{
								name: "gcp.projectId",
								value: $deployment_context.projectId
							}
						]
					}
				end),
			operatorApplications: {
				dxpOperator: {
					values: {
						marketplace: {
							claimName: "dxp-operator-marketplace",
							csi: {
								driver: "filestore.csi.storage.gke.io",
								volumeAttributes: {
									ip: $marketplace_filestore_ip,
									volume: "marketplace"
								},
								volumeHandle: $marketplace_volume_handle
							},
							enabled: true
						},
						podSecurityContext: {
							fsGroup: 1000,
							fsGroupChangePolicy: "OnRootMismatch"
						}
					}
				},
				keda: (
					if $keda_service_account_namespace == "" then
						{}
					else
						{
							enabled: true,
							namespace: $keda_service_account_namespace
						}
					end)
			}
		}
	}
}
| platform_values($configuration; "liferay-gcp-infrastructure-provider"; "liferay-gcp")