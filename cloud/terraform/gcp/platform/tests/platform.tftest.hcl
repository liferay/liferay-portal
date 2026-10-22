mock_provider "google" {
	mock_resource "google_project_iam_custom_role" {
		defaults={
			name="projects/liferay-test-project/roles/liferay_test_backup_controller_abcd"
		}
	}
}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
override_data {
	target=data.google_compute_zones.available
	values={
		names=[
			"us-central1-a",
			"us-central1-b",
		]
	}
}
override_data {
	target=data.google_container_cluster.gke
	values={
		network="projects/liferay-test-project/global/networks/liferay-test-vpc"
		private_cluster_config=[
			{
				master_ipv4_cidr_block="172.16.0.0/28"
			},
		]
	}
}
override_data {
	target=data.google_filestore_instance.marketplace
	values={
		file_shares=[
			{
				name="marketplace"
			},
		]
		name="liferay-test-marketplace"
		networks=[
			{
				ip_addresses=["10.1.0.2"]
			},
		]
	}
}
override_data {
	target=data.google_project.project
	values={
		number="123456789012"
	}
}
run "should_assemble_the_deployment_context" {
	assert {
		condition=join(",", keys(local.deployment_context)) == "deploymentName,projectId,projectNumber,region,vpcName"
		error_message="The deployment context must carry exactly the keys the infrastructure provider consumes"
	}
	assert {
		condition=local.deployment_context.projectNumber == "123456789012"
		error_message="The deployment context must carry the project number the workload identity principals are built from"
	}
	assert {
		condition=local.deployment_context.vpcName == "liferay-test-vpc"
		error_message="The deployment context must carry the name of the cluster network, not its path"
	}
	command=plan
}
run "should_bind_each_workload_identity_to_its_kubernetes_service_account" {
	assert {
		condition=google_project_iam_member.workload_identity["crossplane_data/roles/cloudsql.admin"].member == "principal://iam.googleapis.com/projects/123456789012/locations/global/workloadIdentityPools/liferay-test-project.svc.id.goog/subject/ns/crossplane-system/sa/crossplane-data"
		error_message="The crossplane-data roles must be granted directly to the crossplane-data service account"
	}
	assert {
		condition=google_project_iam_member.workload_identity["crossplane_iam/roles/resourcemanager.projectIamAdmin"].member == "principal://iam.googleapis.com/projects/123456789012/locations/global/workloadIdentityPools/liferay-test-project.svc.id.goog/subject/ns/crossplane-system/sa/crossplane-iam"
		error_message="The crossplane-iam roles must be granted directly to the crossplane-iam service account"
	}
	assert {
		condition=google_project_iam_member.backup_controller.role == google_project_iam_custom_role.backup_controller.name
		error_message="The backup controller must be granted its custom role"
	}
	command=apply
}
run "should_build_the_secret_manager_secret_store_provider" {
	assert {
		condition=local.cluster_secret_store_provider.gcpsm.projectID == "liferay-test-project"
		error_message="The default branch must produce a Secret Manager provider for the deployment project"
	}
	assert {
		condition=join(",", keys(local.cluster_secret_store_provider.gcpsm)) == "projectID"
		error_message="The Secret Manager provider must carry only the project, so it authenticates through the External Secrets controller's workload identity"
	}
	assert {
		condition=google_project_iam_member.external_secrets[0].condition[0].expression == "resource.name.startsWith('projects/123456789012/secrets/liferay-certificates-') || resource.name.startsWith('projects/123456789012/secrets/liferay-credentials-') || resource.name.startsWith('projects/123456789012/secrets/liferay-licenses-')"
		error_message="The External Secrets controller must only read the Liferay secret prefixes"
	}
	command=plan
}
run "should_derive_the_kubernetes_endpoint_cidrs_from_the_cluster" {
	assert {
		condition=output.kubernetes_endpoint_cidrs == ["172.16.0.0/28"]
		error_message="The Kubernetes endpoint CIDRs must be the control plane range of the private cluster"
	}
	command=plan
}
run "should_grant_keda_and_grafana_when_observability_is_enabled" {
	assert {
		condition=google_project_iam_member.workload_identity["grafana/roles/monitoring.viewer"].member == "principal://iam.googleapis.com/projects/123456789012/locations/global/workloadIdentityPools/liferay-test-project.svc.id.goog/subject/ns/observability/sa/grafana"
		error_message="Grafana must read Managed Prometheus as its own service account"
	}
	assert {
		condition=google_project_iam_member.workload_identity["keda/roles/monitoring.viewer"].member == "principal://iam.googleapis.com/projects/123456789012/locations/global/workloadIdentityPools/liferay-test-project.svc.id.goog/subject/ns/scaling/sa/custom-keda"
		error_message="KEDA must read Managed Prometheus as the configured service account"
	}
	assert {
		condition=output.keda_service_account_namespace == "scaling"
		error_message="The KEDA service account namespace must follow the configuration"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
			namespace="scaling"
			service_account_name="custom-keda"
		}
		observability_config={
			enabled=true
		}
	}
}
run "should_inject_an_external_secret_store_provider" {
	assert {
		condition=local.cluster_secret_store_provider.vault.server == "https://vault.example.com"
		error_message="An injected provider must replace the Secret Manager provider"
	}
	assert {
		condition=length(google_project_iam_member.external_secrets) == 0
		error_message="The External Secrets controller must not be granted Secret Manager access for an injected provider"
	}
	command=plan
	variables {
		cluster_secret_store={
			provider_hcl={
				vault={
					server="https://vault.example.com"
				}
			}
		}
	}
}
run "should_look_up_a_regional_cluster_in_the_region" {
	assert {
		condition=data.google_container_cluster.gke.location == "us-central1"
		error_message="A regional cluster must be looked up in the region"
	}
	command=plan
	variables {
		regional_cluster=true
	}
}
run "should_look_up_a_zonal_cluster_in_the_first_zone" {
	assert {
		condition=data.google_container_cluster.gke.location == "us-central1-a"
		error_message="A zonal cluster must be looked up in the first zone of the region, where the gke stack creates it"
	}
	command=plan
}
run "should_omit_keda_and_grafana_by_default" {
	assert {
		condition=length([for key in keys(google_project_iam_member.workload_identity) : key if startswith(key, "grafana/") || startswith(key, "keda/")]) == 0
		error_message="KEDA and Grafana must not be granted anything unless observability is enabled"
	}
	assert {
		condition=output.keda_service_account_namespace == ""
		error_message="The KEDA service account namespace must be empty unless KEDA is enabled"
	}
	command=plan
}
run "should_omit_keda_when_observability_is_disabled" {
	assert {
		condition=!contains(keys(google_project_iam_member.workload_identity), "keda/roles/monitoring.viewer")
		error_message="KEDA reads Managed Prometheus, so it must not be granted access without observability"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
		}
	}
}
run "should_publish_the_marketplace_filestore_share" {
	assert {
		condition=output.marketplace_filestore_ip == "10.1.0.2"
		error_message="The marketplace Filestore IP must come from the instance"
	}
	assert {
		condition=output.marketplace_volume_handle == "modeInstance/us-central1-a/liferay-test-marketplace/marketplace"
		error_message="The marketplace volume handle must name the zone, instance and share in the Filestore CSI format"
	}
	command=plan
}
variables {
	argocd_helm_chart_version="10.1.3"
	deployment_name="liferay-test"
	project_id="liferay-test-project"
	region="us-central1"
}