resource "google_project_iam_member" "observability_grafana_viewer" {
	count=var.observability_config.enabled ? 1 : 0
	member="principal://iam.googleapis.com/projects/${data.google_project.project.number}/locations/global/workloadIdentityPools/${var.project_id}.svc.id.goog/subject/ns/${var.observability_config.namespace}/sa/grafana"
	project=var.project_id
	role="roles/monitoring.viewer"
}