locals {
	argocd_gateway_class_name="argocd-gateway-class"
	argocd_gateway_name="argocd-gateway"
	argocd_tls_enabled=var.argocd_domain_config.hostname != null && var.argocd_domain_config.tls_external_secret_name != null
	argocd_tls_external_secret_name=var.argocd_domain_config.tls_external_secret_name == null ? null : (
		startswith(var.argocd_domain_config.tls_external_secret_name, local.secret_prefixes.certificates) ?
		var.argocd_domain_config.tls_external_secret_name :
		"${local.secret_prefixes.certificates}${var.argocd_domain_config.tls_external_secret_name}"
	)
	argocd_tls_secret_name="argocd-server-tls"
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
		}
	)
	git_repo_infrastructure_separate_from_liferay=local.infrastructure_git_repo_url != var.liferay_git_repo_url
	infrastructure_appproject_name="liferay-infrastructure"
	infrastructure_git_repo_url=coalesce(var.infrastructure_git_repo_config.url, var.liferay_git_repo_url)
	ksa_principal_base="principal://iam.googleapis.com/projects/${data.google_project.project.number}/locations/global/workloadIdentityPools/${var.project_id}.svc.id.goog/subject/ns/${var.crossplane_namespace}/sa"
	liferay_appproject_name="liferay-application"
	liferay_helm_chart_config=merge(
		var.liferay_helm_chart_config,
		{
			chart_name=var.liferay_helm_chart_name
		},
		var.liferay_helm_chart_name == "liferay-default" ? {
			chart_url=coalesce(var.liferay_helm_chart_config.chart_url, "oci://us-central1-docker.pkg.dev/liferay-artifact-registry/liferay-helm-chart/liferay-default")
			region=var.region
			values_scope_prefix=""
		} : {},
		var.liferay_helm_chart_name == "liferay-gcp" ? {
			chart_url=coalesce(var.liferay_helm_chart_config.chart_url, "oci://us-central1-docker.pkg.dev/liferay-artifact-registry/liferay-helm-chart/liferay-gcp")
			region=var.region
			values_scope_prefix="liferay-default."
		} : {},
	)
	liferay_namespace_pattern="liferay-*"
	secret_prefixes={
		certificates="liferay-certificates-"
		licenses="liferay-licenses-"
	}
	secret_store_name="${var.deployment_name}-secret-store"
	secret_store_provider_default={
		gcpsm={
			projectID=var.project_id
		}
	}
	secret_store_provider_default_enabled=var.external_secret_store_provider_hcl == null
	secret_store_provider_hcl=local.secret_store_provider_default_enabled ? local.secret_store_provider_default : var.external_secret_store_provider_hcl
	terraform_manager_name="liferay-cloud-native-terraform"
	vpc_name="${var.deployment_name}-vpc"
}
