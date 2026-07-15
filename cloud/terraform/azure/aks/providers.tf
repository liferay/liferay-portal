provider "azurerm" {
	features {}
}
provider "helm" {
	kubernetes={
		cluster_ca_certificate=base64decode(azurerm_kubernetes_cluster.main.kube_config[0].cluster_ca_certificate)
		exec={
			api_version="client.authentication.k8s.io/v1beta1"
			args=["get-token", "--login", "azurecli", "--server-id", "6dae42f8-4368-4678-94ff-3960e28e3630"]
			command="kubelogin"
		}
		host=azurerm_kubernetes_cluster.main.kube_config[0].host
	}
}
provider "kubernetes" {
	cluster_ca_certificate=base64decode(azurerm_kubernetes_cluster.main.kube_config[0].cluster_ca_certificate)
	exec {
		api_version="client.authentication.k8s.io/v1beta1"
		args=["get-token", "--login", "azurecli", "--server-id", "6dae42f8-4368-4678-94ff-3960e28e3630"]
		command="kubelogin"
	}
	host=azurerm_kubernetes_cluster.main.kube_config[0].host
}