locals {
	cluster_name="${var.deployment_name}-gke"
	first_zone=data.google_compute_zones.available.names[0]
	health_check_cidrs=distinct(
		concat(
			data.google_netblock_ip_ranges.health_checkers.cidr_blocks_ipv4,
			data.google_netblock_ip_ranges.legacy_health_checkers.cidr_blocks_ipv4
		)
	)
	pod_cidr=cidrsubnet(var.vpc_cidr, 4, 1)
	project_number=data.google_project.project.number
	service_cidr=cidrsubnet(var.vpc_cidr, 4, 2)
	subnet_cidr=cidrsubnet(var.vpc_cidr, 4, 0)
	workload_identity_pool="${var.project_id}.svc.id.goog"
}
