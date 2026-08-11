provider "azurerm" {
	features {}
}
terraform {
	backend "azurerm" {}
	required_providers {
		azurerm={
			source="hashicorp/azurerm"
			version="~> 4.0"
		}
	}
	required_version=">=1.10.0"
}