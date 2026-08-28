mock_provider "google" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_configure_argo_workflows_with_defaults" {
	assert {
		condition=helm_release.argo_workflows.version == var.argo_workflows_helm_chart_version
		error_message="The Argo Workflows Helm release must use the configured chart version"
	}
	assert {
		condition=helm_release.argo_workflows.namespace == "argo-workflows-system"
		error_message="The Argo Workflows Helm release must default to the argo-workflows-system namespace"
	}
	assert {
		condition=helm_release.argo_workflows.upgrade_install == true
		error_message="The Argo Workflows Helm release must have upgrade_install enabled"
	}
	assert {
		condition=kubernetes_namespace.argo_workflows.metadata[0].labels["app.kubernetes.io/managed-by"] == "liferay-cloud-native-terraform"
		error_message="The Argo Workflows namespace should carry the Terraform manager label from local.common_labels"
	}
	command=plan
}
run "should_harden_argo_workflows_security_contexts" {
	assert {
		condition=yamldecode(helm_release.argo_workflows.values[0]).controller.workflowDefaults.spec.securityContext.runAsNonRoot == true
		error_message="The workflow default security context must run as nonroot"
	}
	assert {
		condition=contains(yamldecode(helm_release.argo_workflows.values[0]).executor.securityContext.capabilities.drop, "ALL")
		error_message="The executor security context must drop all capabilities"
	}
	assert {
		condition=yamldecode(helm_release.argo_workflows.values[0]).mainContainer.securityContext.runAsUser == 8737
		error_message="The main container must run as the nonroot workflow user"
	}
	command=plan
}
run "should_honor_a_custom_argo_workflows_namespace" {
	assert {
		condition=helm_release.argo_workflows.namespace == "workflows" && kubernetes_namespace.argo_workflows.metadata[0].name == "workflows"
		error_message="A custom argo_workflows_namespace must flow to both the Helm release and the namespace"
	}
	command=plan
	variables {
		argo_workflows_namespace="workflows"
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