provider "aws" {
	default_tags {
		tags={
			DeploymentName=var.deployment_name
		}
	}
	region=var.region
}
provider "kubernetes" {
}
terraform {
	required_providers {
		aws={
			source="hashicorp/aws"
			version="~> 5.0"
		}
		kubernetes={
			source="hashicorp/kubernetes"
			version="~> 2.36.0"
		}
		random={
			source="hashicorp/random"
			version="~> 3.0"
		}
	}
	required_version=">=1.5.0"
}