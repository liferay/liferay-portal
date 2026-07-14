config {
	call_module_type="all"
	force=false
}
plugin "azurerm" {
	enabled=true
	source="github.com/terraform-linters/tflint-ruleset-azurerm"
	version="0.32.0"
}
plugin "terraform" {
	enabled=true
	preset="recommended"
}