resource "google_project_iam_custom_role" "crossplane_cloudplatform" {
	permissions=[
		"iam.serviceAccounts.create",
		"iam.serviceAccounts.delete",
		"iam.serviceAccounts.get",
		"iam.serviceAccounts.getIamPolicy",
		"iam.serviceAccounts.list",
		"iam.serviceAccounts.setIamPolicy",
		"iam.serviceAccounts.update",
		"resourcemanager.projects.getIamPolicy",
		"resourcemanager.projects.setIamPolicy",
	]
	project=var.project_id
	provisioner "local-exec" {
		command="gcloud iam roles delete ${self.role_id} --project ${self.project} --quiet"
		on_failure=continue
		when=destroy
	}
	role_id=replace("${var.deployment_name}_crossplane_cloudplatform", "-", "_")
	title="Liferay Crossplane Cloud Platform Provider Role"
}
resource "google_project_iam_custom_role" "crossplane_kms" {
	permissions=[
		"cloudkms.cryptoKeys.create",
		"cloudkms.cryptoKeys.get",
		"cloudkms.cryptoKeys.getIamPolicy",
		"cloudkms.cryptoKeys.list",
		"cloudkms.cryptoKeys.setIamPolicy",
		"cloudkms.cryptoKeys.update",
		"cloudkms.keyRings.create",
		"cloudkms.keyRings.get",
		"cloudkms.keyRings.list",
	]
	project=var.project_id
	provisioner "local-exec" {
		command="gcloud iam roles delete ${self.role_id} --project ${self.project} --quiet"
		on_failure=continue
		when=destroy
	}
	role_id=replace("${var.deployment_name}_crossplane_kms", "-", "_")
	title="Liferay Crossplane KMS Provider Role"
}
resource "google_project_iam_custom_role" "crossplane_sql" {
	permissions=[
		"cloudsql.databases.create",
		"cloudsql.databases.get",
		"cloudsql.databases.list",
		"cloudsql.databases.update",
		"cloudsql.instances.create",
		"cloudsql.instances.delete",
		"cloudsql.instances.get",
		"cloudsql.instances.list",
		"cloudsql.instances.update",
		"cloudsql.users.create",
		"cloudsql.users.get",
		"cloudsql.users.list",
		"cloudsql.users.update",
	]
	project=var.project_id
	provisioner "local-exec" {
		command="gcloud iam roles delete ${self.role_id} --project ${self.project} --quiet"
		on_failure=continue
		when=destroy
	}
	role_id=replace("${var.deployment_name}_crossplane_sql", "-", "_")
	title="Liferay Crossplane SQL Provider Role"
}
resource "google_project_iam_custom_role" "crossplane_storage" {
	permissions=[
		"storage.buckets.create",
		"storage.buckets.delete",
		"storage.buckets.get",
		"storage.buckets.getIamPolicy",
		"storage.buckets.list",
		"storage.buckets.setIamPolicy",
		"storage.buckets.update",
		"storage.objects.list",
	]
	project=var.project_id
	provisioner "local-exec" {
		command="gcloud iam roles delete ${self.role_id} --project ${self.project} --quiet"
		on_failure=continue
		when=destroy
	}
	role_id=replace("${var.deployment_name}_crossplane_storage", "-", "_")
	title="Liferay Crossplane Storage Provider Role"
}
resource "google_project_iam_member" "crossplane_cloudplatform" {
	condition {
		description="Restrict ProjectIAMMember grants to roles required by Liferay DXP compositions"
		expression="api.getAttribute('iam.googleapis.com/modifiedGrantsByRole', []).hasOnly(['roles/cloudsql.client', 'roles/cloudsql.instanceUser'])"
		title="Restrict roles Crossplane can grant"
	}
	member="${local.ksa_principal_base}/provider-gcp-cloudplatform"
	project=var.project_id
	role=google_project_iam_custom_role.crossplane_cloudplatform.name
}
resource "google_project_iam_member" "crossplane_kms" {
	member="${local.ksa_principal_base}/provider-gcp-kms"
	project=var.project_id
	role=google_project_iam_custom_role.crossplane_kms.name
}
resource "google_project_iam_member" "crossplane_sql" {
	member="${local.ksa_principal_base}/provider-gcp-sql"
	project=var.project_id
	role=google_project_iam_custom_role.crossplane_sql.name
}
resource "google_project_iam_member" "crossplane_storage" {
	member="${local.ksa_principal_base}/provider-gcp-storage"
	project=var.project_id
	role=google_project_iam_custom_role.crossplane_storage.name
}
