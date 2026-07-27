variable "argocd_gateway_config" {
	default=null
	type=object({
		envoy_proxy_service_annotations=optional(map(string), {})
		hostname=string
		tls_external_secret_key=optional(string, null)
	})
}
variable "argocd_sso_credentials_secret_key" {
	default=null
	type=string
}
variable "cluster_secret_store_provider_hcl" {
	default=null
	type=any
}
variable "deployment_name" {
	type=string
	validation {
		condition=can(regex("^[a-z][a-z0-9-]{2,17}$", var.deployment_name))
		error_message="The variable \"deployment_name\" must be 3-18 characters, start with a lowercase letter, and contain only lowercase letters, numbers, and hyphens, so the derived \"<deployment_name>-vault\" Key Vault name fits Azure's 24-character limit."
	}
}
variable "git_repository_config" {
	default={}
	type=object({
		credentials=optional(object({
			github_app_id_property=optional(string, null)
			github_app_installation_id_property=optional(string, null)
			github_app_private_key_property=optional(string, null)
			internal_secret_name=optional(string, null)
			method=optional(string, null)
			secret_name=optional(string, null)
			ssh_private_key_property=optional(string, null)
			token_property=optional(string, null)
			username_property=optional(string, null)
		}), null)
		revision=optional(string, null)
	})
}
variable "git_repository_url" {
	type=string
}
variable "gitops_layout_config" {
	default={}
	type=object({
		base=optional(string, null)
		environment_id=optional(string, null)
		environments=optional(string, null)
		infrastructure_values_filename=optional(string, null)
		liferay_values_filename=optional(string, null)
		project_id=optional(string, null)
		project_values_filename=optional(string, null)
		projects=optional(string, null)
		system=optional(string, null)
	})
}
variable "infrastructure_git_repository_config" {
	default={}
	type=object({
		credentials=optional(object({
			github_app_id_property=optional(string, null)
			github_app_installation_id_property=optional(string, null)
			github_app_private_key_property=optional(string, null)
			internal_secret_name=optional(string, null)
			method=optional(string, null)
			secret_name=optional(string, null)
			ssh_private_key_property=optional(string, null)
			token_property=optional(string, null)
			username_property=optional(string, null)
		}), null)
		revision=optional(string, null)
		url=optional(string, null)
	})
}
variable "infrastructure_helm_chart_config" {
	default={}
	type=object({
		chart_name=optional(string, null)
		chart_url=optional(string, null)
		path=optional(string, null)
	})
}
variable "infrastructure_helm_chart_version" {
	type=string
}
variable "infrastructure_provider_helm_chart_config" {
	default={}
	type=object({
		chart_name=optional(string, null)
		chart_url=optional(string, null)
		path=optional(string, null)
	})
}
variable "infrastructure_provider_helm_chart_version" {
	type=string
}
variable "keda_enabled" {
	default=false
	type=bool
}
variable "liferay_helm_chart_config" {
	default={}
	type=object({
		chart_name=optional(string, null)
		chart_url=optional(string, null)
		path=optional(string, null)
	})
}
variable "liferay_helm_chart_version" {
	type=string
}
variable "observability_enabled" {
	default=false
	type=bool
}
variable "observability_helm_chart_config" {
	default={}
	type=object({
		chart_name=optional(string, null)
		chart_url=optional(string, null)
		path=optional(string, null)
	})
}
variable "observability_helm_chart_version" {
	type=string
}
variable "platform_helm_chart_config" {
	default={}
	type=object({
		chart_name=optional(string, null)
		chart_url=optional(string, null)
		path=optional(string, null)
	})
}
variable "platform_helm_chart_version" {
	type=string
}
variable "region" {
	type=string
}