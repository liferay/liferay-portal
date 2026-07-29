resource "google_container_cluster" "primary" {
	addons_config {
		gcp_filestore_csi_driver_config {
			enabled=true
		}
		horizontal_pod_autoscaling {
			disabled=false
		}
		http_load_balancing {
			disabled=true
		}
		network_policy_config {
			disabled=true
		}
	}
	binary_authorization {
		evaluation_mode="PROJECT_SINGLETON_POLICY_ENFORCE"
	}
	datapath_provider="ADVANCED_DATAPATH"
	deletion_protection=var.deletion_protection
	depends_on=[google_compute_subnetwork.subnet]
	dynamic "authenticator_groups_config" {
		content {
			security_group=var.gke_security_group
		}
		for_each=var.gke_security_group!=null ? [1] : []
	}
	enable_intranode_visibility=true
	enable_shielded_nodes=true
	initial_node_count=1
	ip_allocation_policy {
		cluster_secondary_range_name="${var.deployment_name}-pods"
		services_secondary_range_name="${var.deployment_name}-services"
	}
	location=var.regional_cluster ? var.region : local.first_zone
	master_auth {
		client_certificate_config {
			issue_client_certificate=false
		}
	}
	master_authorized_networks_config {
		dynamic "cidr_blocks" {
			content {
				cidr_block=cidr_blocks.value
			}
			for_each=var.master_authorized_networks
		}
	}
	monitoring_config {
		enable_components=["SYSTEM_COMPONENTS"]
		managed_prometheus {
			enabled=true
		}
	}
	name=local.cluster_name
	network=google_compute_network.vpc.id
	networking_mode="VPC_NATIVE"
	node_pool_defaults {
		node_config_defaults {
			logging_variant="DEFAULT"
		}
	}
	private_cluster_config {
		enable_private_endpoint=true
		enable_private_nodes=true
		master_global_access_config {
			enabled=false
		}
		master_ipv4_cidr_block=var.master_ipv4_cidr_block
	}
	project=var.project_id
	release_channel {
		channel="REGULAR"
	}
	remove_default_node_pool=true
	resource_labels={
		deployment_name=var.deployment_name
		managed_by="terraform"
	}
	security_posture_config {
		mode="BASIC"
		vulnerability_mode="VULNERABILITY_BASIC"
	}
	subnetwork=google_compute_subnetwork.subnet.id
	workload_identity_config {
		workload_pool=local.workload_identity_pool
	}
}
resource "google_container_node_pool" "general_purpose" {
	autoscaling {
		max_node_count=var.max_node_count
		min_node_count=var.min_node_count
	}
	cluster=google_container_cluster.primary.name
	location=var.regional_cluster ? var.region : local.first_zone
	management {
		auto_repair=true
		auto_upgrade=true
	}
	name="general-purpose"
	node_config {
		disk_size_gb=100
		disk_type="pd-balanced"
		image_type="COS_CONTAINERD"
		kubelet_config {
			insecure_kubelet_readonly_port_enabled="FALSE"
		}
		machine_type=var.machine_type
		metadata={
			disable-legacy-endpoints="true"
		}
		oauth_scopes=["https://www.googleapis.com/auth/cloud-platform",]
		preemptible=var.spot_instances
		service_account=google_service_account.node_sa.email
		shielded_instance_config {
			enable_integrity_monitoring=true
			enable_secure_boot=true
		}
		workload_metadata_config {
			mode="GKE_METADATA"
		}
	}
	project=var.project_id
}
resource "google_gke_hub_membership" "membership" {
	authority {
    	issuer = "https://container.googleapis.com/v1/${google_container_cluster.primary.id}"
  	}
	depends_on=[
		google_container_cluster.primary,
		google_container_node_pool.general_purpose,
	]
	endpoint {
		gke_cluster {
			resource_link="//container.googleapis.com/${google_container_cluster.primary.id}"
		}
	}
	membership_id="${var.deployment_name}-membership"
	project=var.project_id
}
resource "google_project_iam_member" "node_permissions" {
	member="serviceAccount:${google_service_account.node_sa.email}"
	project=var.project_id
	role="roles/container.defaultNodeServiceAccount"
}
resource "google_service_account" "node_sa" {
	account_id="${var.deployment_name}-node-sa"
	project=var.project_id
}
