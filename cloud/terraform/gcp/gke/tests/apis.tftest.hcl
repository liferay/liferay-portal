mock_provider "google" {}
mock_provider "google-beta" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "null" {}
mock_provider "time" {}
override_data {
	target=data.google_compute_zones.available
	values={
		names=["us-central1-a", "us-central1-b", "us-central1-c"]
	}
}
override_data {
	target=data.google_netblock_ip_ranges.health_checkers
	values={
		cidr_blocks_ipv4=["35.191.0.0/16"]
	}
}
override_data {
	target=data.google_netblock_ip_ranges.legacy_health_checkers
	values={
		cidr_blocks_ipv4=["130.211.0.0/22"]
	}
}
override_data {
	target=data.google_project.project
	values={
		number="1234567890"
	}
}
run "should_enable_required_project_apis" {
	assert {
		condition=google_project_service.apis["connectgateway.googleapis.com"].service == "connectgateway.googleapis.com" && google_project_service.apis["container.googleapis.com"].service == "container.googleapis.com"
		error_message="The GKE and Connect Gateway APIs must be enabled"
	}
	assert {
		condition=google_project_service.apis["container.googleapis.com"].disable_on_destroy == false
		error_message="Project APIs must not be disabled on destroy"
	}
	assert {
		condition=length(google_project_service.apis) == 20
		error_message="All required project APIs must be enabled"
	}
	command=plan
}
run "should_grant_storage_transfer_agent_roles" {
	assert {
		condition=google_project_iam_member.storagetransfer_agent_permissions["roles/storage.admin"].member == "serviceAccount:project-1234567890@storage-transfer-service.iam.gserviceaccount.com"
		error_message="The Storage Transfer roles must bind the project's Storage Transfer service agent"
	}
	assert {
		condition=length(google_project_iam_member.storagetransfer_agent_permissions) == 2
		error_message="The Storage Transfer agent must be granted both required roles"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	envoy_gateway_helm_chart_version="1.6.3"
	project_id="liferay-test-project"
	region="us-central1"
}