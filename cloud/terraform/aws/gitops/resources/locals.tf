locals {
	account_id=data.aws_caller_identity.current.account_id
	alloy_role_arn=var.observability_config.enabled ? "arn:aws:iam::${local.account_id}:role/${var.deployment_name}-alloy" : ""
	argocd_external_url=var.argocd_domain_config.hostname == null ? "" : "${local.argocd_tls_enabled ? "https" : "http"}://${var.argocd_domain_config.hostname}"
	argocd_gateway_class_name="argocd-gateway-class"
	argocd_gateway_name="argocd-gateway"
	argocd_source_ranges=distinct(concat([data.aws_vpc.current.cidr_block], var.argocd_additional_allowed_cidr_blocks))
	argocd_tls_enabled=var.argocd_domain_config.hostname != null && var.argocd_domain_config.tls_external_secret_name != null
	argocd_tls_external_secret_name=var.argocd_domain_config.tls_external_secret_name == null ? null : (
		startswith(var.argocd_domain_config.tls_external_secret_name, local.secret_prefixes.certificates) ?
		var.argocd_domain_config.tls_external_secret_name :
		"${local.secret_prefixes.certificates}${var.argocd_domain_config.tls_external_secret_name}"
	)
	argocd_tls_secret_name="argocd-server-tls"
	aws_marketplace_enabled=var.liferay_helm_chart_name == "liferay-aws-marketplace"
	cluster_name="${var.deployment_name}-eks"
	common_labels={
		"app.kubernetes.io/component"="gitops-infrastructure"
		"app.kubernetes.io/managed-by"=local.terraform_manager_name
		"app.kubernetes.io/part-of"="liferay-gitops"
		"environment"="internal"
		"liferay.com/project"="liferay-cloud-native"
	}
	default_crossplane_container_security_context={
		allowPrivilegeEscalation=false
		capabilities={
			drop=["ALL"]
		}
		privileged=false
		readOnlyRootFilesystem=true
	}
	default_crossplane_pod_security_context={
		fsGroup=2000
		runAsGroup=2000
		runAsNonRoot=true
		runAsUser=2000
		seccompProfile={
			type="RuntimeDefault"
		}
	}
	deploymentruntimeconfig_opentelemetry_annotations={
		"instrumentation.opentelemetry.io/inject-dotnet"="false"
		"instrumentation.opentelemetry.io/inject-java"="false"
		"instrumentation.opentelemetry.io/inject-nodejs"="false"
		"instrumentation.opentelemetry.io/inject-python"="false"
		"sidecar.opentelemetry.io/inject"="false"
	}
	dxp_operator_parameters=concat(
		var.dxp_operator_config.heartbeat_interval == null ? [] : [
			{
				name="liferay-dxp-operator.heartbeatInterval"
				value=var.dxp_operator_config.heartbeat_interval
			}
		],
		var.dxp_operator_config.image.repository == null ? [] : [
			{
				name="liferay-dxp-operator.image.repository"
				value=var.dxp_operator_config.image.repository
			}
		],
		var.dxp_operator_config.image.tag == null ? [] : [
			{
				name="liferay-dxp-operator.image.tag"
				value=var.dxp_operator_config.image.tag
			}
		],
		var.dxp_operator_config.provisioning_base_url == null ? [] : [
			{
				name="liferay-dxp-operator.provisioning.baseURL"
				value=var.dxp_operator_config.provisioning_base_url
			}
		],
		var.dxp_operator_config.retry_max_delay == null ? [] : [
			{
				name="liferay-dxp-operator.retry.maxDelay"
				value=var.dxp_operator_config.retry_max_delay
			}
		])
	ecr_credentials_sync_image="alpine/k8s:1.29.1"
	ecr_credentials_sync_schedule="0 */8 * * *"
	ecr_credentials_sync_script=<<-EOT
		set -euo pipefail

		TOKEN=$(aws ecr get-login-password --region ${local.liferay_helm_chart_config.region})

		if [ -z "$TOKEN" ]; then
			echo "Failed to fetch ECR token"
			exit 1
		fi

		kubectl create secret generic argocd-ecr-credentials \
			--dry-run=client -o yaml \
			--from-literal=enableOCI=true \
			--from-literal=name=liferay-helm-chart \
			--from-literal=password="$TOKEN" \
			--from-literal=project=${local.liferay_appproject_name} \
			--from-literal=type=helm \
			--from-literal=url=${local.liferay_helm_chart_config.chart_url} \
			--from-literal=username=AWS \
			--namespace ${var.argocd_namespace} | kubectl apply -f -

		kubectl label secret argocd-ecr-credentials argocd.argoproj.io/secret-type=repository \
			--namespace ${var.argocd_namespace} \
			--overwrite
	EOT
	ecr_credentials_sync_serviceaccount_name="ecr-credentials-sync-sa"
	eks_endpoint_cidrs=[for s in data.aws_subnet.private : s.cidr_block]
	envoy_proxy_role_arn="arn:aws:iam::${local.account_id}:role/${var.deployment_name}-envoy-proxy"
	gateway_class_name="liferay-gateway-class"
	gateway_name="${var.infrastructure_git_repo_config.target.slugProjectId}-${var.infrastructure_git_repo_config.target.slugEnvironmentId}-gateway"
	git_repo_auth_configs=merge(
		local.git_repo_infrastructure_separate_from_liferay ? {
			"infrastructure"=merge(
				var.infrastructure_git_repo_config.auth,
				{
					url=local.infrastructure_git_repo_url
				})
		} : {},
		{
			"liferay"=merge(
				var.liferay_git_repo_config.auth,
				{
					url=var.liferay_git_repo_url
				})
		})
	git_repo_infrastructure_separate_from_liferay=local.infrastructure_git_repo_url != var.liferay_git_repo_url
	grafana_role_arn=var.observability_config.enabled ? "arn:aws:iam::${local.account_id}:role/${var.deployment_name}-grafana" : ""
	infrastructure_appproject_name="liferay-infrastructure"
	infrastructure_git_repo_url=coalesce(var.infrastructure_git_repo_config.url, var.liferay_git_repo_url)
	liferay_appproject_name="liferay-application"
	liferay_helm_chart_config=merge(
		var.liferay_helm_chart_config,
		{
			chart_name=var.liferay_helm_chart_name
		},
		var.liferay_helm_chart_name == "liferay-default" ? {
			chart_url=coalesce(var.liferay_helm_chart_config.chart_url, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-default")
			ecr_credentials_sync_required=false
			region=var.region
			values_scope_prefix=""
		} : {},
		var.liferay_helm_chart_name == "liferay-aws" ? {
			chart_url=coalesce(var.liferay_helm_chart_config.chart_url, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-aws")
			ecr_credentials_sync_required=false
			region=var.region
			values_scope_prefix="liferay-default."
		} : {},
		var.liferay_helm_chart_name == "liferay-aws-marketplace" ? {
			chart_url=coalesce(var.liferay_helm_chart_config.chart_url, "709825985650.dkr.ecr.us-east-1.amazonaws.com")
			ecr_credentials_sync_required=true
			region="us-east-1"
			values_scope_prefix="liferay-aws.liferay-default."
		} : {},
	)
	liferay_namespace_pattern="liferay-*"
	liferay_service_account_role_arn="arn:aws:iam::${local.account_id}:role/${local.liferay_service_account_role_name}"
	liferay_service_account_role_name="${var.deployment_name}-irsa"
	oidc_provider=replace(data.aws_eks_cluster.cluster.identity[0].oidc[0].issuer, "https://", "")
	rds_exporter_role_arn=var.observability_config.enabled ? "arn:aws:iam::${local.account_id}:role/${var.deployment_name}-rds-exporter" : ""
	secret_prefixes={
		certificates="liferay-certificates-"
		credentials="liferay-credentials-"
		licenses="liferay-licenses-"
	}
	secret_store_name="${var.deployment_name}-secret-store"
	secret_store_provider_default={
		aws={
			auth={
				jwt={
					serviceAccountRef={
						name="secret-store-sa"
						namespace=var.external_secrets_namespace
					}
				}
			}
			region=var.region
			service="SecretsManager"
		}
	}
	secret_store_provider_default_enabled=var.external_secret_store_provider_hcl == null
	secret_store_provider_hcl=local.secret_store_provider_default_enabled ? local.secret_store_provider_default : var.external_secret_store_provider_hcl
	terraform_manager_name="liferay-cloud-native-terraform"
}