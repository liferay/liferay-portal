terraform {
	required_providers {
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