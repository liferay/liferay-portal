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
run "should_not_accept_an_unsupported_helm_chart_name" {
	command=plan
	expect_failures=[var.liferay_helm_chart_name]
	variables {
		liferay_helm_chart_name="liferay-aws"
	}
}
run "should_use_the_liferay_default_chart" {
	assert {
		condition=local.liferay_helm_chart_config.chart_url == "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-default"
		error_message="The liferay-default chart must default to the liferay-default OCI chart URL"
	}
	assert {
		condition=local.liferay_helm_chart_config.values_scope_prefix == ""
		error_message="The liferay-default chart must not have scopes defined"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-default"
	}
}
run "should_use_the_liferay_gcp_chart_by_default" {
	assert {
		condition=local.liferay_helm_chart_config.chart_url == "oci://us-central1-docker.pkg.dev/external-assets-prd/liferay-helm-chart/liferay-gcp"
		error_message="The liferay-gcp chart must default to the liferay-gcp OCI chart URL"
	}
	assert {
		condition=local.liferay_helm_chart_config.values_scope_prefix == "liferay-default." && local.liferay_helm_chart_config.region == "us-central1"
		error_message="The liferay-gcp chart must scope values under liferay-default and must use the deployment region"
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