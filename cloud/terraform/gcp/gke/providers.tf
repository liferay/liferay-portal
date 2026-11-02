data "google_client_config" "default" {}
provider "google" {
	default_labels={
		deployment_name=var.deployment_name
	}
	project=var.project_id
	region=var.region
}
provider "google-beta" {
	default_labels={
		deployment_name=var.deployment_name
	}
	project=var.project_id
	region=var.region
}
terraform {
	backend "gcs" {}
	required_providers {
		google={
			source="hashicorp/google"
			version="~> 7.0"
		}
		google-beta={
			source="hashicorp/google-beta"
			version="~> 7.0"
		}
		null={
			source="hashicorp/null"
			version="~> 3.0"
		}
		time={
			source="hashicorp/time"
			version="~> 0.12"
		}
	}
	required_version=">= 1.5.0"
}