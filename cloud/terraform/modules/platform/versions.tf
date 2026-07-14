terraform {
	required_providers {
		kubernetes={
			source="hashicorp/kubernetes"
			version=">= 2.36.0"
		}
	}
	required_version=">=1.10.0"
}