data "google_client_config" "default" {}
data "google_compute_zones" "available" {
	region=var.region
}
data "google_filestore_instance" "marketplace" {
	location=local.filestore_zone
	name="${var.deployment_name}-marketplace"
}
data "google_project" "project" {
	project_id=var.project_id
}
