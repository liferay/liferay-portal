provider "azurerm" {
	features {}
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
		kubernetes={
			source="hashicorp/kubernetes"
			version="~> 3.1.0"
		}
	}
	required_version=">=1.10.0"
}