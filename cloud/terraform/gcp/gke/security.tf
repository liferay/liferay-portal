resource "google_compute_firewall" "allow_health_checks" {
	allow {
		ports=["443", "80", "8080",]
		protocol="tcp"
	}
	name="${var.deployment_name}-allow-health-checks"
	network=google_compute_network.vpc.name
	priority=1000
	source_ranges=local.health_check_cidrs
}
resource "google_compute_firewall" "allow_internal" {
	allow {
		ports=[
			"53",
			"80",
			"443",
			"8080",
			"8443",
			"9080",
			"9443",
			"10250",
			"10255",
			"10256",
		]
		protocol="tcp"
	}
	allow {
		ports=[
			"53",
			"4789",
			"7800",
			"8472",
		]
		protocol="udp"
	}
	allow {
		protocol="icmp"
	}
	name="${var.deployment_name}-allow-internal"
	network=google_compute_network.vpc.name
	priority=1000
	source_ranges=[local.pod_cidr, local.service_cidr, local.subnet_cidr,]
}
resource "google_compute_firewall" "allow_master" {
	allow {
		ports=["443", "10250", "9443",]
		protocol="tcp"
	}
	name="${var.deployment_name}-allow-master"
	network=google_compute_network.vpc.name
	priority=1000
	source_ranges=[var.master_ipv4_cidr_block,]
}
resource "google_compute_firewall" "envoy_ingress_managed" {
	allow {
		ports=var.envoy_listener_ports
		protocol="tcp"
	}
	name="${var.deployment_name}-envoy-ingress"
	network=google_compute_network.vpc.name
	priority=1000
	source_ranges=[var.vpc_cidr,]
}