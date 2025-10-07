provider "aws" {
	default_tags {
		tags={
			DeploymentName=var.deployment_name
		}
	}
	region=var.region
}
terraform {
	required_providers {
		aws={
			source="hashicorp/aws"
			version="~> 5.0"
		}
	}
	required_version=">=1.5.0"
}