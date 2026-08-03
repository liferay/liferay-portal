provider "azurerm" {
	features {}
}
provider "helm" {
	kubernetes={
	}
}
provider "kubernetes" {
}
terraform {
	backend "azurerm" {}
	required_providers {
		azurerm={
			source="hashicorp/azurerm"
			version="~> 4.0"
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