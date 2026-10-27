output "cluster_name" {
	value=google_container_cluster.primary.name
}
output "deployment_name" {
	value=var.deployment_name
}
output "gateway_namespace" {
	value=var.gateway_namespace
}
output "membership_name" {
	value=google_gke_hub_membership.membership.name
}
output "network_id" {
	value=google_compute_network.vpc.id
}
output "network_name" {
	value=google_compute_network.vpc.name
}
output "private_subnet_ids" {
	value=[google_compute_subnetwork.subnet.id,]
}
output "project_id" {
	value=var.project_id
}
output "region" {
	value=var.region
}
output "subnet_id" {
	value=google_compute_subnetwork.subnet.id
}
output "subnet_name" {
	value=google_compute_subnetwork.subnet.name
}