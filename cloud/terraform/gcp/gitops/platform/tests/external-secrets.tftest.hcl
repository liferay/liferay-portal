mock_provider "google" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_configure_external_secrets_with_defaults" {
	assert {
		condition=helm_release.external_secrets.version == var.external_secrets_helm_chart_version
		error_message="The External Secrets Helm release must use the configured chart version"
	}
	assert {
		condition=helm_release.external_secrets.namespace == "external-secrets-system"
		error_message="The External Secrets Helm release must default to the external-secrets-system namespace"
	}
	assert {
		condition=helm_release.external_secrets.create_namespace == true
		error_message="The External Secrets Helm release must create its namespace"
	}
	assert {
		condition=yamldecode(helm_release.external_secrets.values[0]).installCRDs == true
		error_message="The External Secrets Helm release must install its CRDs"
	}
	command=plan
}
run "should_honor_a_custom_external_secrets_namespace" {
	assert {
		condition=helm_release.external_secrets.namespace == "eso"
		error_message="A custom external_secrets_namespace must flow to the Helm release"
	}
	command=plan
	variables {
		external_secrets_namespace="eso"
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