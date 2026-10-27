resource "google_project_iam_member" "secrets_accessor_permissions" {
	condition {
		expression=join(" || ", concat(
			[
				"resource.name.startsWith('projects/${data.google_project.project.number}/secrets/${local.secret_prefixes.certificates}')",
				"resource.name.startsWith('projects/${data.google_project.project.number}/secrets/${local.secret_prefixes.licenses}')",
			],
			[
				for git_repo_auth_config in local.git_repo_auth_configs : "resource.name.startsWith('projects/${data.google_project.project.number}/secrets/${git_repo_auth_config.credentials_secret_name}')"
			]
		))
		title="liferay_external_secrets_access"
	}
	count=local.secret_store_provider_default_enabled ? 1 : 0
	member="principal://iam.googleapis.com/projects/${data.google_project.project.number}/locations/global/workloadIdentityPools/${var.project_id}.svc.id.goog/subject/ns/${var.external_secrets_namespace}/sa/external-secrets"
	project=var.project_id
	role="roles/secretmanager.secretAccessor"
}