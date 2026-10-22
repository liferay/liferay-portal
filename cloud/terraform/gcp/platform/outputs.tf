output "cluster_secret_store_provider" {
	value=local.cluster_secret_store_provider
}
output "deployment_context" {
	value=local.deployment_context
}
output "keda_service_account_namespace" {
	value=local.keda_enabled ? var.keda_config.namespace : ""
}
output "kubernetes_endpoint_cidrs" {
	value=[data.google_container_cluster.gke.private_cluster_config[0].master_ipv4_cidr_block]
}
output "marketplace_filestore_ip" {
	value=data.google_filestore_instance.marketplace.networks[0].ip_addresses[0]
}
output "marketplace_volume_handle" {
	value="modeInstance/${local.first_zone}/${data.google_filestore_instance.marketplace.name}/${data.google_filestore_instance.marketplace.file_shares[0].name}"
}