mock_provider "google" {}
mock_provider "google-beta" {}
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
run "should_allow_health_check_traffic_from_deduplicated_cidrs" {
	assert {
		condition=contains(local.health_check_cidrs, "130.211.0.0/22") && contains(local.health_check_cidrs, "35.191.0.0/16") && length(local.health_check_cidrs) == 2
		error_message="health_check_cidrs must be the deduplicated union of both health checker netblocks"
	}
	assert {
		condition=google_compute_firewall.allow_health_checks.name == "liferay-test-allow-health-checks"
		error_message="The health check firewall name must be derived from deployment_name"
	}
	command=plan
}
run "should_allow_internal_traffic_from_cluster_cidrs" {
	assert {
		condition=contains(google_compute_firewall.allow_internal.source_ranges, local.pod_cidr) && contains(google_compute_firewall.allow_internal.source_ranges, local.service_cidr) && contains(google_compute_firewall.allow_internal.source_ranges, local.subnet_cidr)
		error_message="The internal firewall must allow traffic from the pod, service, and subnet CIDRs"
	}
	command=plan
}
run "should_allow_master_traffic_from_the_master_cidr" {
	assert {
		condition=contains(google_compute_firewall.allow_master.source_ranges, "172.16.0.0/28")
		error_message="The master firewall must allow traffic from master_ipv4_cidr_block"
	}
	command=plan
}
run "should_open_envoy_ingress_ports" {
	assert {
		condition=contains(google_compute_firewall.envoy_ingress_managed.source_ranges, "10.0.0.0/16")
		error_message="The Envoy ingress firewall must be scoped to the VPC CIDR"
	}
	assert {
		condition=google_compute_firewall.envoy_ingress_managed.name == "liferay-test-envoy-ingress"
		error_message="The Envoy ingress firewall name must be derived from deployment_name"
	}
	assert {
		condition=length([for a in google_compute_firewall.envoy_ingress_managed.allow : a if contains(a.ports, "10080") && contains(a.ports, "10443")]) == 1
		error_message="The Envoy ingress firewall must open ports 10080 and 10443"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	project_id="liferay-test-project"
	region="us-central1"
}