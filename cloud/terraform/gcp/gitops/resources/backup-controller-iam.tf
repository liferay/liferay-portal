resource "google_project_iam_custom_role" "backup_controller_custom_role" {
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
	project=var.project_id
	role_id=replace("${var.deployment_name}_backup_controller_${random_id.backup_controller_role_id.hex}", "-", "_")
	title="Liferay Backup Controller Role"
}
resource "google_project_iam_member" "backup_controller_iam_member" {
	member="${local.ksa_principal_base}/backup-controller"
	project=var.project_id
	role=google_project_iam_custom_role.backup_controller_custom_role.name
}
resource "random_id" "backup_controller_role_id" {
	byte_length=2
}