mock_provider "google" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_configure_crossplane_with_defaults" {
	assert {
		condition=helm_release.crossplane.version == var.crossplane_helm_chart_version
		error_message="The Crossplane Helm release must use the configured chart version"
	}
	assert {
		condition=helm_release.crossplane.namespace == "crossplane-system"
		error_message="The Crossplane Helm release must default to the crossplane-system namespace"
	}
	assert {
		condition=helm_release.crossplane.atomic == true && helm_release.crossplane.cleanup_on_fail == true && helm_release.crossplane.create_namespace == true && helm_release.crossplane.wait == true
		error_message="The Crossplane Helm release must be atomic, clean up on failure, create its namespace, and wait"
	}
	assert {
		condition=yamldecode(helm_release.crossplane.values[0]).resourcesCrossplane.limits.memory == "2Gi"
		error_message="The Crossplane controller must request its configured memory limit"
	}
	command=plan
}
run "should_honor_a_custom_crossplane_namespace" {
	assert {
		condition=helm_release.crossplane.namespace == "xplane"
		error_message="A custom crossplane_namespace must flow to the Helm release"
	}
	command=plan
	variables {
		crossplane_namespace="xplane"
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