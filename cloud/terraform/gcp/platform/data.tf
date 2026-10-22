data "google_compute_zones" "available" {
	region=var.region
}
data "google_container_cluster" "gke" {
	location=var.regional_cluster ? var.region : local.first_zone
	name="${var.deployment_name}-gke"
}
data "google_filestore_instance" "marketplace" {
	location=local.first_zone
	name="${var.deployment_name}-marketplace"
}
data "google_project" "project" {
	project_id=var.project_id
}