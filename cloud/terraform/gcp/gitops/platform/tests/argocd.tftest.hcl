mock_provider "google" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_configure_the_helm_release_with_defaults" {
	assert {
		condition=helm_release.argocd.version == var.argocd_helm_chart_version
		error_message="The ArgoCD Helm release must use the configured chart version"
	}
	assert {
		condition=length(helm_release.argocd.values) == 1
		error_message="The ArgoCD values list must contain exactly one document"
	}
	assert {
		condition=can(yamldecode(helm_release.argocd.values[0]).configs.cm["resource.customizations.health.gcp.liferay.com_LiferayInfrastructure"])
		error_message="The ArgoCD values must register the GCP LiferayInfrastructure health check"
	}
	command=plan
}
run "should_create_namespace_and_secret_with_defaults" {
	assert {
		condition=kubernetes_namespace.argocd.metadata[0].name == "argocd-system"
		error_message="The ArgoCD namespace should default to argocd-system"
	}
	assert {
		condition=kubernetes_namespace.argocd.metadata[0].labels["app.kubernetes.io/managed-by"] == "liferay-cloud-native-terraform"
		error_message="The ArgoCD namespace should carry the Terraform manager label from local.common_labels"
	}
	assert {
		condition=kubernetes_namespace.argocd.metadata[0].labels["environment"] == "internal"
		error_message="The ArgoCD namespace should carry the internal environment label"
	}
	assert {
		condition=kubernetes_secret.argocd_secret.metadata[0].labels["app.kubernetes.io/managed-by"] == "Helm"
		error_message="The argocd-secret resource must override the managed-by label to Helm so Helm adopts it"
	}
	command=plan
}
run "should_honor_a_custom_argocd_namespace" {
	assert {
		condition=kubernetes_namespace.argocd.metadata[0].name == "gitops"
		error_message="The ArgoCD namespace must honor a custom argocd_namespace"
	}
	assert {
		condition=helm_release.argocd.namespace == "gitops" && output.argocd_namespace == "gitops"
		error_message="A custom argocd_namespace must flow to the Helm release and the module output"
	}
	command=plan
	variables {
		argocd_namespace="gitops"
	}
}
variables {
	argo_workflows_helm_chart_version="2.0.3"
	argocd_helm_chart_version="9.5.16"
	crossplane_helm_chart_version="2.1.3"
	deployment_name="liferay-test"
	external_secrets_helm_chart_version="1.0.0"
	keda_helm_chart_version="2.19.0"
	project_id="liferay-test-project"
	region="us-central1"
}