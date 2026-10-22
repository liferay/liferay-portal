resource "google_project_iam_custom_role" "backup_controller" {
	permissions=[
		"cloudsql.backupRuns.create",
		"cloudsql.backupRuns.delete",
		"cloudsql.databases.list",
		"cloudsql.instances.export",
		"cloudsql.instances.get",
		"cloudsql.instances.list",
		"cloudsql.instances.update",
		"storage.buckets.get",
		"storage.buckets.list",
		"storage.objects.delete",
		"storage.objects.list",
		"storagetransfer.jobs.create",
		"storagetransfer.jobs.delete",
		"storagetransfer.jobs.get",
		"storagetransfer.jobs.run",
		"storagetransfer.operations.get",
		"storagetransfer.operations.list",
		"storagetransfer.projects.getServiceAccount",
	]
	role_id=replace("${var.deployment_name}_backup_controller_${random_id.backup_controller_role_id.hex}", "-", "_")
	title="Liferay Backup Controller Role"
}
resource "google_project_iam_member" "backup_controller" {
	member=local.workload_identity_members.backup_controller
	project=var.project_id
	role=google_project_iam_custom_role.backup_controller.name
}
resource "google_project_iam_member" "external_secrets" {
	condition {
		expression=join(" || ", [for secret_prefix in local.secret_prefixes : "resource.name.startsWith('projects/${local.project_number}/secrets/${secret_prefix}')"])
		title="liferay_external_secrets_access"
	}
	count=local.default_secret_manager_enabled ? 1 : 0
	member=local.workload_identity_members.external_secrets
	project=var.project_id
	role="roles/secretmanager.secretAccessor"
}
resource "google_project_iam_member" "workload_identity" {
	for_each=local.workload_identity_bindings
	member=local.workload_identity_members[each.value.identity]
	project=var.project_id
	role=each.value.role
}
resource "random_id" "backup_controller_role_id" {
	byte_length=2
}