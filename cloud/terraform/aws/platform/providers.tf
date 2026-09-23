provider "aws" {
	default_tags {
		tags={
			DeploymentName=var.deployment_name
		}
	}
	region=var.region
}
provider "helm" {
	kubernetes={
	}
}
provider "kubernetes" {
}
terraform {
	backend "s3" {}
	required_providers {
		aws={
			source="hashicorp/aws"
			version="~> 6.43.0"
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