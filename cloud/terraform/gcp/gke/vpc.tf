resource "google_compute_global_address" "private_ip_alloc" {
	address_type="INTERNAL"
	depends_on=[time_sleep.wait_for_apis]
	name="${var.deployment_name}-psa-range"
	network=google_compute_network.vpc.id
	prefix_length=16
	project=var.project_id
	purpose="VPC_PEERING"
}
resource "google_compute_network" "vpc" {
	auto_create_subnetworks=false
	depends_on=[time_sleep.wait_for_apis]
	name="${var.deployment_name}-vpc"
	project=var.project_id
	routing_mode="GLOBAL"
}
resource "google_compute_route" "egress_internet" {
	dest_range="0.0.0.0/0"
	name="${var.deployment_name}-egress-internet"
	network=google_compute_network.vpc.name
	next_hop_gateway="default-internet-gateway"
	priority=1000
	project=var.project_id
	tags=["egress-inet",]
}
resource "google_compute_router" "router" {
	name="${var.deployment_name}-router"
	network=google_compute_network.vpc.name
	project=var.project_id
	region=var.region
}
resource "google_compute_router_nat" "nat" {
	log_config {
		enable=true
		filter="ERRORS_ONLY"
	}
	name="${var.deployment_name}-nat"
	nat_ip_allocate_option="AUTO_ONLY"
	project=var.project_id
	region=var.region
	router=google_compute_router.router.name
	source_subnetwork_ip_ranges_to_nat="ALL_SUBNETWORKS_ALL_IP_RANGES"
}
resource "google_compute_subnetwork" "subnet" {
	ip_cidr_range=local.subnet_cidr
	log_config {
		aggregation_interval="INTERVAL_10_MIN"
		flow_sampling=0.5
		metadata="INCLUDE_ALL_METADATA"
	}
	name="${var.deployment_name}-subnet"
	network=google_compute_network.vpc.id
	private_ip_google_access=true
	project=var.project_id
	region=var.region
	secondary_ip_range {
		ip_cidr_range=local.pod_cidr
		range_name="${var.deployment_name}-pods"
	}
	secondary_ip_range {
		ip_cidr_range=local.service_cidr
		range_name="${var.deployment_name}-services"
	}
}
resource "google_service_networking_connection" "private_vpc_connection" {
	network=google_compute_network.vpc.id
	reserved_peering_ranges=[google_compute_global_address.private_ip_alloc.name]
	service="servicenetworking.googleapis.com"
}