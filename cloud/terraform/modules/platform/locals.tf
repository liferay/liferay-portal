locals {
	argocd_helm_values={
		for key, value in {
			gateway=var.argocd_gateway_config == null ? null : {
				enabled=true
				envoyProxy={
					serviceAnnotations=var.argocd_gateway_config.envoy_proxy_service_annotations
				}
				hostname=var.argocd_gateway_config.hostname
				tls={
					externalSecretKey=var.argocd_gateway_config.tls_external_secret_key == null ? "" : var.argocd_gateway_config.tls_external_secret_key
				}
			}
			sso=var.argocd_sso_credentials_secret_key == null ? null : {
				credentialsSecretKey=var.argocd_sso_credentials_secret_key
				enabled=true
			}
		} : key=>value if value != null
	}
	argocd_namespace="argocd-system"
	chart_sources={
		for name, source in {
			infrastructure={
				config=var.infrastructure_helm_chart_config
				default_chart_name="liferay-${var.cloud_provider}-infrastructure"
				version=var.infrastructure_helm_chart_version
			}
			infrastructure_provider={
				config=var.infrastructure_provider_helm_chart_config
				default_chart_name="liferay-${var.cloud_provider}-infrastructure-provider"
				version=var.infrastructure_provider_helm_chart_version
			}
			liferay={
				config=var.liferay_helm_chart_config
				default_chart_name="liferay-${var.cloud_provider}"
				version=var.liferay_helm_chart_version
			}
			observability={
				config=var.observability_helm_chart_config
				default_chart_name="observability"
				version=var.observability_helm_chart_version
			}
			platform={
				config=var.platform_helm_chart_config
				default_chart_name="liferay-platform"
				version=var.platform_helm_chart_version
			}
		} : name=>merge(
			{
				repoURL=coalesce(source.config.chart_url, "${local.helm_chart_registry_url}/${coalesce(source.config.chart_name, source.default_chart_name)}")
				targetRevision=source.version
			},
			source.config.path == null ? {
				chart=coalesce(source.config.chart_name, source.default_chart_name)
			} : {
				path=source.config.path
			}
		)
	}
	common_labels={
		"app.kubernetes.io/managed-by"=local.terraform_manager_name
		"liferay.com/project"="liferay-cloud-native"
	}
	git_repository={
		for key, value in {
			credentialsSecretName=try(var.git_repository_config.credentials.secret_name, null)
			githubAppIdProperty=try(var.git_repository_config.credentials.github_app_id_property, null)
			githubAppInstallationIdProperty=try(var.git_repository_config.credentials.github_app_installation_id_property, null)
			githubAppPrivateKeyProperty=try(var.git_repository_config.credentials.github_app_private_key_property, null)
			internalSecretName=try(var.git_repository_config.credentials.internal_secret_name, null)
			method=try(var.git_repository_config.credentials.method, null)
			revision=var.git_repository_config.revision
			sshPrivateKeyProperty=try(var.git_repository_config.credentials.ssh_private_key_property, null)
			tokenProperty=try(var.git_repository_config.credentials.token_property, null)
			usernameProperty=try(var.git_repository_config.credentials.username_property, null)
		} : key=>value if value != null
	}
	gitops_layout={
		for key, value in {
			base=var.gitops_layout_config.base
			environmentId=var.gitops_layout_config.environment_id
			environments=var.gitops_layout_config.environments
			infrastructureValuesFilename=var.gitops_layout_config.infrastructure_values_filename
			liferayValuesFilename=var.gitops_layout_config.liferay_values_filename
			projectId=var.gitops_layout_config.project_id
			projectValuesFilename=var.gitops_layout_config.project_values_filename
			projects=var.gitops_layout_config.projects
			system=var.gitops_layout_config.system
		} : key=>value if value != null
	}
	helm_chart_registry_url="oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart"
	infrastructure_git_repository={
		for key, value in {
			credentialsSecretName=try(var.infrastructure_git_repository_config.credentials.secret_name, null)
			githubAppIdProperty=try(var.infrastructure_git_repository_config.credentials.github_app_id_property, null)
			githubAppInstallationIdProperty=try(var.infrastructure_git_repository_config.credentials.github_app_installation_id_property, null)
			githubAppPrivateKeyProperty=try(var.infrastructure_git_repository_config.credentials.github_app_private_key_property, null)
			internalSecretName=try(var.infrastructure_git_repository_config.credentials.internal_secret_name, null)
			method=try(var.infrastructure_git_repository_config.credentials.method, null)
			revision=var.infrastructure_git_repository_config.revision
			sshPrivateKeyProperty=try(var.infrastructure_git_repository_config.credentials.ssh_private_key_property, null)
			tokenProperty=try(var.infrastructure_git_repository_config.credentials.token_property, null)
			url=var.infrastructure_git_repository_config.url
			usernameProperty=try(var.infrastructure_git_repository_config.credentials.username_property, null)
		} : key=>value if value != null
	}
	platform_helm_values=merge(
		{
			clusterIdentity=var.cluster_identity
			clusterSecretStore={
				enabled=true
				provider=var.cluster_secret_store_provider
			}
			gitops=merge(
				{
					repository=merge(
						local.git_repository,
						{
							url=var.git_repository_url
						}
					)
				},
				length(local.infrastructure_git_repository) == 0 ? {} : {
					infrastructureRepository=local.infrastructure_git_repository
				},
				length(local.gitops_layout) == 0 ? {} : {
					layout=local.gitops_layout
				}
			)
			infrastructure=local.chart_sources.infrastructure
			infrastructureProvider=local.chart_sources.infrastructure_provider
			liferay=local.chart_sources.liferay
			observability=merge(
				{
					enabled=var.observability_enabled
					parameters=[
						{
							name="cloudProvider"
							value=var.cloud_provider
						},
					]
				},
				local.chart_sources.observability
			)
		},
		length(local.argocd_helm_values) == 0 ? {} : {
			argocd=local.argocd_helm_values
		},
		length(var.operator_applications_helm_values) == 0 ? {} : {
			operatorApplications=var.operator_applications_helm_values
		}
	)
	terraform_manager_name="liferay-cloud-native-terraform"
}