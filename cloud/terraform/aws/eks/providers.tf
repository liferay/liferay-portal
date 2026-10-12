provider "aws" {
	default_tags {
		tags={
			DeploymentName=var.deployment_name
		}
	}
	region=var.region
}
provider "kubernetes" {
	cluster_ca_certificate=base64decode(module.eks.cluster_certificate_authority_data)
	exec {
		api_version="client.authentication.k8s.io/v1beta1"
		args=[
			"eks",
			"get-token",
			"--cluster-name",
			module.eks.cluster_name,
			"--region",
			var.region,
		]
		command="aws"
	}
	host=module.eks.cluster_endpoint
}
terraform {
	backend "s3" {}
	required_providers {
		aws={
			source="hashicorp/aws"
			version="~> 6.43.0"
		}
		kubernetes={
			source="hashicorp/kubernetes"
			version="~> 3.1.0"
		}
		time={
			source="hashicorp/time"
			version="~> 0.13.1"
		}
	}
	required_version=">=1.10.0"
}