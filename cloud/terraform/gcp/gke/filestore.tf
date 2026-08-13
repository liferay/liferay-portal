resource "google_filestore_instance" "marketplace" {
	depends_on=[google_service_networking_connection.private_vpc_connection]
	file_shares {
		capacity_gb=1024
		name="marketplace"
	}
	location=local.first_zone
	name="${var.deployment_name}-marketplace"
	networks {
		connect_mode="PRIVATE_SERVICE_ACCESS"
		modes=["MODE_IPV4"]
		network=google_compute_network.vpc.id
		reserved_ip_range=google_compute_global_address.private_ip_alloc.name
	}
	tier="BASIC_HDD"
}
