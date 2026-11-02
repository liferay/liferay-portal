output "cluster_name" {
	value=google_container_cluster.primary.name
}
output "membership_name" {
	value=google_gke_hub_membership.membership.name
}
output "project_id" {
	value=var.project_id
}
output "region" {
	value=var.region
}