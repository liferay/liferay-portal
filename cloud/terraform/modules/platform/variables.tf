variable "argocd_gateway_config" {
	default=null
	type=object({
		envoy_proxy_service_annotations=optional(map(string), {})
		hostname=string
		tls_external_secret_key=optional(string, null)
	})
	validation {
		condition=var.argocd_gateway_config == null || can(regex("^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)+$", var.argocd_gateway_config.hostname))
		error_message="The \"argocd_gateway_config.hostname\" value must be a lowercase DNS hostname."
	}
}
variable "argocd_sso_credentials_secret_key" {
	default=null
	type=string
}
variable "cloud_provider" {
	type=string
	validation {
		condition=can(regex("^[a-z]+$", var.cloud_provider))
		error_message="The \"cloud_provider\" value must be a lowercase cloud provider slug such as \"aws\", \"azure\", or \"gcp\"."
	}
}
variable "cluster_identity" {
	type=map(string)
}
variable "cluster_secret_store_provider" {
	type=any
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
	validation {
		condition=try(var.git_repository_config.credentials.method, null) == null || contains(["github_app", "https", "ssh"], var.git_repository_config.credentials.method)
		error_message="The \"git_repository_config.credentials.method\" value must be \"github_app\", \"https\", or \"ssh\"."
	}
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
	validation {
		condition=var.infrastructure_git_repository_config.credentials == null || var.infrastructure_git_repository_config.url != null
		error_message="The \"infrastructure_git_repository_config.credentials\" value requires \"infrastructure_git_repository_config.url\"."
	}
	validation {
		condition=try(var.infrastructure_git_repository_config.credentials.method, null) == null || contains(["github_app", "https", "ssh"], var.infrastructure_git_repository_config.credentials.method)
		error_message="The \"infrastructure_git_repository_config.credentials.method\" value must be \"github_app\", \"https\", or \"ssh\"."
	}
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
variable "operator_applications_helm_values" {
	default={}
	type=any
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