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
run "should_derive_cluster_name_from_deployment_name" {
	assert {
		condition=google_container_cluster.primary.name == "liferay-test-gke"
		error_message="The GKE cluster name must be derived from deployment_name"
	}
	assert {
		condition=google_service_account.node_sa.account_id == "liferay-test-node-sa"
		error_message="The node service account ID must be derived from deployment_name"
	}
	assert {
		condition=output.cluster_name == "liferay-test-gke"
		error_message="The cluster_name output must mirror the cluster resource name"
	}
	command=plan
}
run "should_disable_control_plane_logging_when_turned_off" {
	assert {
		condition=!contains(google_container_cluster.primary.logging_config[0].enable_components, "APISERVER") && !contains(google_container_cluster.primary.logging_config[0].enable_components, "CONTROLLER_MANAGER") && !contains(google_container_cluster.primary.logging_config[0].enable_components, "SCHEDULER")
		error_message="Disabling control plane logging must drop APISERVER, CONTROLLER_MANAGER and SCHEDULER"
	}
	assert {
		condition=contains(google_container_cluster.primary.logging_config[0].enable_components, "SYSTEM_COMPONENTS") && contains(google_container_cluster.primary.logging_config[0].enable_components, "WORKLOADS") && length(google_container_cluster.primary.logging_config[0].enable_components) == 2
		error_message="Disabling control plane logging must keep the components GKE enables by default"
	}
	command=plan
	variables {
		control_plane_logging_config={
			enabled=false
		}
	}
}
run "should_enable_authenticator_groups_with_a_security_group" {
	assert {
		condition=google_container_cluster.primary.authenticator_groups_config[0].security_group == "gke-security-groups@example.com"
		error_message="The authenticator groups config must use the supplied security group"
	}
	assert {
		condition=length(google_container_cluster.primary.authenticator_groups_config) == 1
		error_message="Setting gke_security_group must emit an authenticator_groups_config block"
	}
	command=plan
	variables {
		gke_security_group="gke-security-groups@example.com"
	}
}
run "should_enable_control_plane_logging_by_default" {
	assert {
		condition=contains(google_container_cluster.primary.logging_config[0].enable_components, "APISERVER") && contains(google_container_cluster.primary.logging_config[0].enable_components, "CONTROLLER_MANAGER") && contains(google_container_cluster.primary.logging_config[0].enable_components, "SCHEDULER")
		error_message="Control plane logging must be enabled by default"
	}
	assert {
		condition=contains(google_container_cluster.primary.logging_config[0].enable_components, "SYSTEM_COMPONENTS") && contains(google_container_cluster.primary.logging_config[0].enable_components, "WORKLOADS") && length(google_container_cluster.primary.logging_config[0].enable_components) == 5
		error_message="Enabling control plane logging must not switch off the components GKE enables by default"
	}
	command=plan
}
run "should_locate_a_regional_cluster_in_the_region" {
	assert {
		condition=google_container_cluster.primary.location == "us-central1"
		error_message="A regional cluster must be located in the region, not a zone"
	}
	assert {
		condition=google_container_node_pool.general_purpose.location == "us-central1"
		error_message="The node pool must follow the cluster's regional location"
	}
	command=plan
	variables {
		regional_cluster=true
	}
}
run "should_make_nodes_preemptible_with_spot_instances" {
	assert {
		condition=google_container_node_pool.general_purpose.node_config[0].preemptible == true
		error_message="Enabling spot_instances must mark node pool nodes as preemptible"
	}
	command=plan
	variables {
		spot_instances=true
	}
}
run "should_not_accept_deployment_name_shorter_than_3_characters" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="ab"
	}
}
run "should_not_accept_uppercase_deployment_name" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="Liferay-Test"
	}
}
run "should_pin_a_non_regional_cluster_to_the_first_zone" {
	assert {
		condition=google_container_cluster.primary.location == "us-central1-a"
		error_message="A nonregional cluster must be pinned to the first available zone"
	}
	assert {
		condition=length(google_container_cluster.primary.authenticator_groups_config) == 0
		error_message="No authenticator groups config should be present by default"
	}
	command=plan
}
run "should_set_node_autoscaling_bounds" {
	assert {
		condition=google_container_node_pool.general_purpose.autoscaling[0].max_node_count == 8 && google_container_node_pool.general_purpose.autoscaling[0].min_node_count == 2
		error_message="Node pool autoscaling bounds must follow max_node_count/min_node_count"
	}
	command=plan
	variables {
		max_node_count=8
		min_node_count=2
	}
}
variables {
	deployment_name="liferay-test"
	envoy_gateway_helm_chart_version="1.6.3"
	project_id="liferay-test-project"
	region="us-central1"
}