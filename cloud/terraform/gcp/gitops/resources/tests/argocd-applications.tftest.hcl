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
run "should_name_the_appprojects" {
	assert {
		condition=kubernetes_manifest.infrastructure_applicationset.manifest.spec.template.spec.project == "liferay-infrastructure"
		error_message="The infrastructure ApplicationSet template must target the liferay-infrastructure project"
	}
	assert {
		condition=kubernetes_manifest.infrastructure_appproject.manifest.metadata.name == "liferay-infrastructure"
		error_message="The infrastructure AppProject must be named liferay-infrastructure"
	}
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.project == "liferay-application"
		error_message="The Liferay ApplicationSet template must target the liferay-application project"
	}
	assert {
		condition=kubernetes_manifest.liferay_appproject.manifest.metadata.name == "liferay-application"
		error_message="The Liferay AppProject must be named liferay-application"
	}
	command=plan
}
run "should_pass_deployment_identity_to_the_provider_application" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "deploymentName" && p.value == "liferay-test"
		]) == 1
		error_message="The infrastructure provider Application must pass the deployment name as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "global.gcp.projectId" && p.value == "liferay-test-project"
		]) == 1
		error_message="The infrastructure provider Application must pass the GCP project id as a Helm parameter"
	}
	command=plan
}
run "should_leave_the_operator_settings_to_the_chart_by_default" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.heartbeatInterval" || startswith(p.name, "liferay-dxp-operator.image.") || p.name == "liferay-dxp-operator.provisioning.baseURL" || p.name == "liferay-dxp-operator.retry.maxDelay"
		]) == 0
		error_message="The infrastructure provider Application must not override operator settings unless they are configured"
	}
	command=plan
}
run "should_pass_the_configured_operator_settings_to_the_provider_application" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.image.repository" && p.value == "registry.example.com/liferay-dxp-operator"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured operator image repository as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.image.tag" && p.value == "1.2.3"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured operator image tag as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.heartbeatInterval" && p.value == "90s"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured heartbeat interval as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.provisioning.baseURL" && p.value == "https://provisioning.example.com"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured provisioning base URL as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "liferay-dxp-operator.retry.maxDelay" && p.value == "4m"
		]) == 1
		error_message="The infrastructure provider Application must pass the configured retry maximum delay as a Helm parameter"
	}
	command=plan
	variables {
		dxp_operator_config={
			heartbeat_interval="90s"
			image={
				repository="registry.example.com/liferay-dxp-operator"
				tag="1.2.3"
			}
			provisioning_base_url="https://provisioning.example.com"
			retry_max_delay="4m"
		}
	}
}
run "should_scope_liferay_applicationset_helm_values_by_prefix" {
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters[0].name == "liferay-default.environmentId"
		error_message="The liferay-gcp chart must include prefix for liferay-default chart at environmentId Helm parameter"
	}
	command=plan
}
run "should_use_an_unscoped_prefix_for_the_liferay_default_chart" {
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters[0].name == "environmentId"
		error_message="The liferay-default chart must not include prefix at environmentId Helm parameter"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-default"
	}
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
run "should_leave_the_replica_count_to_the_operator" {
	assert {
		condition=length([
			for difference in kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.ignoreDifferences : difference
			if difference.kind == "StatefulSet" && contains(difference.managedFieldsManagers, "liferay-dxp-operator")
		]) == 1
		error_message="The Liferay ApplicationSet template must ignore spec.replicas on the workload, since the operator caps it against the licensed ceiling"
	}
	command=plan
}
