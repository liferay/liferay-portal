provider "grafana" {
	auth=var.grafana_workspace_api_key
	url="https://${var.grafana_workspace_endpoint}"
}
terraform {
	required_providers {
		grafana={
			source="grafana/grafana"
			version="~> 3.15.2"
		}
	}
}