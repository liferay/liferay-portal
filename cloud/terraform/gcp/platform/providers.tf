provider "google" {
	default_labels={
		deployment_name=var.deployment_name
	}
	project=var.project_id
	region=var.region
}
provider "helm" {
	kubernetes={
	}
}
provider "kubernetes" {
}
terraform {
	backend "gcs" {}
	required_providers {
		google={
			source="hashicorp/google"
			version="~> 7.43.0"
		}
		helm={
			source="hashicorp/helm"
			version="~> 3.1.1"
		}
		kubernetes={
			source="hashicorp/kubernetes"
			version="~> 3.1.0"
		}
		random={
			source="hashicorp/random"
			version="~> 3.8.1"
		}
	}
	required_version=">=1.10.0"
}