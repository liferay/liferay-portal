provider "google" {
	default_labels={
		deployment_name=var.deployment_name
	}
	project=var.project_id
	region=var.region
}
provider "helm" {
	kubernetes={
		host="https://connectgateway.googleapis.com/v1/projects/${data.google_project.project.number}/locations/global/gkeMemberships/${var.deployment_name}-membership"
		token=data.google_client_config.default.access_token
	}
}
provider "kubernetes" {
	host="https://connectgateway.googleapis.com/v1/projects/${data.google_project.project.number}/locations/global/gkeMemberships/${var.deployment_name}-membership"
	token=data.google_client_config.default.access_token
}
terraform {
	backend "gcs" {}
	required_providers {
		google={
			source="hashicorp/google"
			version="~> 6.0"
		}
		helm={
			source="hashicorp/helm"
			version="~> 3.1"
		}
		kubernetes={
			source="hashicorp/kubernetes"
			version="~> 2.36.0"
		}
		random={
			source="hashicorp/random"
			version="~> 3.6"
		}
	}
	required_version=">=1.5.0"
}
