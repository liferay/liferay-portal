include "_platform_values" {search: "./"};

$configuration[0] as $configuration
| . as $outputs
| $outputs.deployment_context.value as $deployment_context
| $outputs.kubernetes_endpoint_cidrs.value as $kubernetes_endpoint_cidrs
| $outputs.marketplace_volume_handle.value as $marketplace_volume_handle
| ($outputs.prometheus_workspace_id.value // "") as $prometheus_workspace_id
| ($configuration.marketplace // false) as $marketplace
| (if $marketplace then "liferay-aws-marketplace" else "liferay-aws" end) as $liferay_chart_name
| (if $marketplace then "liferay-aws.liferay-default." else "liferay-default." end) as $values_prefix
| {
	"service.beta.kubernetes.io/aws-load-balancer-healthcheck-protocol": "TCP",
	"service.beta.kubernetes.io/aws-load-balancer-manage-backend-security-group-rules": "false",
	"service.beta.kubernetes.io/aws-load-balancer-scheme": "internet-facing",
	"service.beta.kubernetes.io/aws-load-balancer-type": "nlb"
} as $operator_service_annotations
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
			gateway: {
				envoyProxy: {
					serviceAnnotations: {
						"service.beta.kubernetes.io/aws-load-balancer-additional-resource-tags": "DeploymentName=\($deployment_context.deploymentName)",
						"service.beta.kubernetes.io/aws-load-balancer-attributes": "load_balancing.cross_zone.enabled=true",
						"service.beta.kubernetes.io/aws-load-balancer-healthcheck-protocol": "TCP",
						"service.beta.kubernetes.io/aws-load-balancer-manage-backend-security-group-rules": "false",
						"service.beta.kubernetes.io/aws-load-balancer-nlb-target-type": "ip",
						"service.beta.kubernetes.io/aws-load-balancer-scheme": "internet-facing",
						"service.beta.kubernetes.io/aws-load-balancer-target-group-attributes": "proxy_protocol_v2.enabled=true",
						"service.beta.kubernetes.io/aws-load-balancer-type": "external"
					}
				}
			},
			infrastructure: {
				parameters: [
					{
						name: "gateway.clientTrafficPolicySpec.enableProxyProtocol",
						value: "true"
					},
					{
						name: "persistence.storageClassName",
						value: "gp3"
					}
				]
			},
			liferay: {
				parameters: [
					{
						name: "\($values_prefix)marketplace.csi.volumeHandle",
						value: $marketplace_volume_handle
					},
					{
						name: "\($values_prefix)networkPolicy.cluster.kubernetesEndpointCidrs",
						value: ($kubernetes_endpoint_cidrs | join(","))
					}
				],
				valuesPrefix: $values_prefix
			},
			networkPolicy: {
				apiServerSources: [
					$kubernetes_endpoint_cidrs[]
					| {
						ipBlock: {
							cidr: .
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
								name: "alloy.enabled",
								value: "true"
							},
							{
								name: "alloy.iam.awsRoleArn",
								value: $outputs.alloy_role_arn.value
							},
							{
								name: "alloy.scrape.cloudwatchRDS.enabled",
								value: "true"
							},
							{
								name: "alloy.scrape.rdsExporter.enabled",
								value: "true"
							},
							{
								name: "aws.ampWorkspaceID",
								value: $prometheus_workspace_id
							},
							{
								name: "aws.ampWorkspaceURL",
								value: $outputs.prometheus_workspace_endpoint.value
							},
							{
								name: "aws.region",
								value: $deployment_context.region
							},
							{
								name: "cloudProvider",
								value: "aws"
							},
							{
								name: "grafana-alloy.namespaceOverride",
								value: "observability"
							},
							{
								name: "grafana.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn",
								value: $outputs.grafana_role_arn.value
							},
							{
								name: "rdsExporter.enabled",
								value: "true"
							},
							{
								name: "rdsExporter.iam.awsRoleArn",
								value: $outputs.rds_exporter_role_arn.value
							}
						]
					}
				end),
			operatorApplications: ({
				dxpOperator: {
					values: {
						marketplace: {
							claimName: "dxp-operator-marketplace",
							csi: {
								driver: "efs.csi.aws.com",
								volumeHandle: $marketplace_volume_handle
							},
							enabled: true
						}
					}
				},
				externalSecrets: {
					values: {
						serviceAccount: {
							annotations: {
								"eks.amazonaws.com/role-arn": $outputs.external_secrets_role_arn.value
							}
						}
					}
				},
				keda: (
					($outputs.keda_role_arn.value // "") as $role_arn
					| if $role_arn == "" then
						{}
					else
						{
							enabled: true,
							namespace: ($outputs.keda_service_account_namespace.value // "keda-system"),
							values: {
								podIdentity: {
									aws: {
										irsa: {
											enabled: true,
											roleArn: $role_arn
										}
									}
								}
							}
						}
					end)
			} + (
				($outputs.ack_prometheusservice_role_arn.value // "") as $role_arn
				| if $role_arn == "" then
					{}
				else
					{
						ackPrometheusService: {
							enabled: true,
							values: {
								aws: {
									region: $deployment_context.region
								},
								serviceAccount: {
									annotations: {
										"eks.amazonaws.com/role-arn": $role_arn
									}
								}
							}
						}
					}
				end))
		}
	}
}
| platform_values($configuration; "liferay-aws-infrastructure-provider"; $liferay_chart_name)