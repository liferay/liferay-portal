terraform {
	required_providers {
		azurerm={
			source="hashicorp/azurerm"
			version="~> 4.0"
		}
		helm={
			source="hashicorp/helm"
			version="~> 3.1"
		}
		kubernetes={
			source="hashicorp/kubernetes"
			version="~> 2.36"
		}
		time={
			source="hashicorp/time"
			version="~> 0.12"
		}
	}
	required_version=">=1.10.0"
}