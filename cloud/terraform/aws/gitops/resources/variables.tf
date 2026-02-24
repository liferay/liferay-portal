variable "argocd_namespace" {
	default="argocd"
}
variable "crossplane_namespace" {
	default="crossplane-system"
}
variable "deployment_name" {
	validation {
		condition=can(regex("^[a-z0-9-]*$", var.deployment_name))
		error_message="The deployment_name must contain only lowercase letters, numbers, and hyphens."
	}
}
variable "external_secrets_namespace" {
	default="external-secrets"
}
variable "infrastructure_git_repo_config" {
	default={
		auth={}
		source_paths={}
		target={}
	}
	type=object(
		{
			auth=object({
				internal_secret_name=optional(string, "argocd-infrastructure-git-credentials")
				method=optional(string, "https")
				secret_store_provider_hcl=optional(any, null)
				ssh_private_key_vault_secret_property=optional(string, "git_ssh_private_key")
				token_vault_secret_property=optional(string, "git_access_token")
				username_vault_secret_property=optional(string, "git_machine_user_id")
				vault_secret_name=optional(string, "liferay-cloud-native/gitops-repo-credentials")
			})
			revision=optional(string, "HEAD")
			source_paths=object({
				base=optional(string, "liferay/projects/{{path[2]}}/base")
				environments=optional(string, "liferay/projects/*/environments/*")
				values_filename=optional(string, "infrastructure.yaml")
			})
			target=object({
				name=optional(string, "{{path[2]}}-{{path[4]}}-infra")
				namespaceSuffix=optional(string, "{{path[2]}}-{{path[4]}}")
				slugEnvironmentId=optional(string, "{{path[4]}}")
				slugProjectId=optional(string, "{{path[2]}}")
			})
			url=optional(string, null)
		})
	validation {
		condition=(
			!contains(keys(var.infrastructure_git_repo_config.auth), "method") ||
			contains(["https", "ssh"], var.infrastructure_git_repo_config.auth.method))
		error_message="The 'infrastructure_git_repo_auth_config.method' value must be 'https' or 'ssh'."
	}
}
variable "infrastructure_helm_chart_config" {
	default={}
	type=object(
		{
			chart=optional(string, "liferay-aws-infrastructure")
			path=optional(string, null)
			repo_url=optional(string, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-aws-infrastructure")
			target_revision=optional(string, "0.2.0")
		})
}
variable "infrastructure_provider_helm_chart_config" {
	default={}
	type=object(
		{
			chart=optional(string, "liferay-aws-infrastructure-provider")
			path=optional(string, null)
			repo_url=optional(string, "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-aws-infrastructure-provider")
			target_revision=optional(string, "0.2.0")
		})
}
variable "liferay_git_repo_config" {
	default={
		auth={}
		source_paths={}
		target={}
	}
	type=object(
		{
			auth=object({
				internal_secret_name=optional(string, "argocd-liferay-git-credentials")
				method=optional(string, "https")
				secret_store_provider_hcl=optional(any, null)
				ssh_private_key_vault_secret_property=optional(string, "git_ssh_private_key")
				token_vault_secret_property=optional(string, "git_access_token")
				username_vault_secret_property=optional(string, "git_machine_user_id")
				vault_secret_name=optional(string, "liferay-cloud-native/gitops-repo-credentials")
			})
			revision=optional(string, "HEAD")
			source_paths=object({
				base=optional(string, "liferay/projects/{{path[2]}}/base")
				environments=optional(string, "liferay/projects/*/environments/*")
				values_filename=optional(string, "liferay.yaml")
			})
			target=object({
				name=optional(string, "{{path[2]}}-{{path[4]}}-app")
				namespaceSuffix=optional(string, "{{path[2]}}-{{path[4]}}")
				slugEnvironmentId=optional(string, "{{path[4]}}")
				slugProjectId=optional(string, "{{path[2]}}")
			})
		})
	validation {
		condition=(
			!contains(keys(var.liferay_git_repo_config.auth), "method") ||
			contains(["https", "ssh"], var.liferay_git_repo_config.auth.method))
		error_message="The 'liferay_git_repo_auth_config.method' value must be 'https' or 'ssh'."
	}
}
variable "liferay_git_repo_url" {
	type=string
}
variable "liferay_helm_chart_config" {
	type=object({
		chart=optional(string, "liferay-aws")
		path=optional(string, null)
		repo_url=optional(string, null)
		target_revision=string
	})
	validation {
		condition=contains(
			[
				"liferay-default",
				"liferay-aws",
				"liferay-aws-marketplace",
			],
			var.liferay_helm_chart_config.chart)
		error_message="The 'chart' value must be 'liferay-default', 'liferay-aws', or 'liferay-aws-marketplace'."
	}
}
variable "region" {
	type=string
}