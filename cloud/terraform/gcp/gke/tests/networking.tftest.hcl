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
run "should_compute_subnet_cidrs" {
	assert {
		condition=google_compute_subnetwork.subnet.ip_cidr_range == "10.0.0.0/20"
		error_message="The primary subnet range must be cidrsubnet(vpc_cidr, 4, 0)"
	}
	assert {
		condition=local.pod_cidr == "10.0.16.0/20"
		error_message="pod_cidr must be cidrsubnet(vpc_cidr, 4, 1)"
	}
	assert {
		condition=local.service_cidr == "10.0.32.0/20"
		error_message="service_cidr must be cidrsubnet(vpc_cidr, 4, 2)"
	}
	command=plan
}
run "should_name_network_resources_from_deployment_name" {
	assert {
		condition=google_compute_global_address.private_ip_alloc.name == "liferay-test-psa-range"
		error_message="The private service access range name must be derived from deployment_name"
	}
	assert {
		condition=google_compute_network.vpc.name == "liferay-test-vpc"
		error_message="The VPC network name must be derived from deployment_name"
	}
	assert {
		condition=google_compute_router.router.name == "liferay-test-router" && google_compute_router_nat.nat.name == "liferay-test-nat"
		error_message="The Cloud Router and NAT names must be derived from deployment_name"
	}
	assert {
		condition=google_compute_subnetwork.subnet.name == "liferay-test-subnet"
		error_message="The subnet name must be derived from deployment_name"
	}
	command=plan
}
run "should_name_secondary_ip_ranges_from_deployment_name" {
	assert {
		condition=google_compute_subnetwork.subnet.secondary_ip_range[0].ip_cidr_range == "10.0.16.0/20" && google_compute_subnetwork.subnet.secondary_ip_range[1].ip_cidr_range == "10.0.32.0/20"
		error_message="The subnet secondary ranges must use the computed pod and service CIDRs"
	}
	assert {
		condition=google_container_cluster.primary.ip_allocation_policy[0].cluster_secondary_range_name == "liferay-test-pods"
		error_message="The pod secondary range name must be derived from deployment_name"
	}
	assert {
		condition=google_container_cluster.primary.ip_allocation_policy[0].services_secondary_range_name == "liferay-test-services"
		error_message="The services secondary range name must be derived from deployment_name"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	project_id="liferay-test-project"
	region="us-central1"
}