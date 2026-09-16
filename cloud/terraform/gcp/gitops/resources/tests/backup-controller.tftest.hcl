mock_provider "google" {
	mock_resource "google_service_account" {
		defaults={
			email="crossplane-gsa@liferay-test-project.iam.gserviceaccount.com"
			name="projects/liferay-test-project/serviceAccounts/crossplane-gsa@liferay-test-project.iam.gserviceaccount.com"
		}
	}
}
mock_provider "helm" {}
mock_provider "kubernetes" {}
override_data {
	target=data.google_project.project
	values={
		number="1234567890"
	}
}
override_resource {
	override_during=plan
	target=random_id.backup_controller_role_id
	values={
		hex="a1b2"
	}
}
run "should_bind_the_backup_controller_role_to_the_ksa" {
	assert {
		condition=endswith(google_project_iam_member.backup_controller_iam_member.member, "/sa/backup-controller")
		error_message="The backup controller binding must target the backup-controller workload identity principal"
	}
	command=plan
}
run "should_create_the_backup_controller_custom_role" {
	assert {
		condition=contains(google_project_iam_custom_role.backup_controller_custom_role.permissions, "cloudsql.backupRuns.create")
		error_message="The backup controller role must grant Cloud SQL backup permissions"
	}
	assert {
		condition=google_project_iam_custom_role.backup_controller_custom_role.role_id == "liferay_test_backup_controller_a1b2"
		error_message="The backup controller role ID must replace hyphens with underscores and carry the per deployment suffix"
	}
	assert {
		condition=google_project_iam_custom_role.backup_controller_custom_role.title == "Liferay Backup Controller Role"
		error_message="The backup controller role must carry its expected title"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	infrastructure_helm_chart_version="0.4.9"
	infrastructure_provider_helm_chart_version="0.3.12"
	liferay_git_repo_url="https://github.com/example/liferay-gitops.git"
	liferay_helm_chart_version="0.4.20"
	observability_helm_chart_version="0.1.0"
	project_id="liferay-test-project"
	region="us-central1"
}