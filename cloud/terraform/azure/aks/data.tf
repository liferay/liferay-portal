data "azurerm_client_config" "current" {}
data "azurerm_kubernetes_service_versions" "current" {
	include_preview=false
	location=var.region
}