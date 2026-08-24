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
run "should_scope_the_manual_network_policies_correctly" {
	assert {
		condition=length(yamldecode(helm_release.argo_workflows.values[0]).extraObjects) == 2
		error_message="Two extra manifests are expected: the workflow-controller metrics ingress and the namespace-wide default-deny — the argo-workflows chart has zero native NetworkPolicy support, so everything here has to be written by hand"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o.kind == "NetworkPolicy"])
		error_message="Every extraObjects entry must be a NetworkPolicy"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : !contains(keys(o.metadata), "namespace")])
		error_message="Every extraObjects NetworkPolicy must omit metadata.namespace so it inherits the Helm release namespace"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o.metadata.labels == local.common_labels])
		error_message="Every extraObjects NetworkPolicy must carry only local.common_labels — app.kubernetes.io/name is irrelevant for this context"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o if o.metadata.name == "argo-workflows-metrics-ingress"][0].spec.podSelector.matchLabels == { "app.kubernetes.io/instance"="argo-workflows", "app.kubernetes.io/name"="argo-workflows-workflow-controller" }
		error_message="argo-workflows-metrics-ingress must select only the workflow-controller pod — confirmed live that argo-workflows-server exposes no metrics port at all, only web:2746"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o if o.metadata.name == "argo-workflows-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argo-workflows-metrics-ingress must allow only the configured observability namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o if o.metadata.name == "argo-workflows-metrics-ingress"][0].spec.ingress[0].ports[0].port == "metrics"
		error_message="argo-workflows-metrics-ingress must scope its allow to the metrics-named port"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec.podSelector == {}
		error_message="default-deny-ingress must have an empty podSelector (matches every pod in the namespace, including argo-workflows-server, which has no legitimate cross-namespace ingress today — no Gateway or in-cluster consumer wires it up)"
	}
	assert {
		condition=!contains(keys([for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec), "ingress")
		error_message="default-deny-ingress must declare zero ingress rules — any ingress key at all would allow something"
	}
	command=plan
}
run "should_honor_a_custom_observability_namespace" {
	assert {
		condition=[for o in yamldecode(helm_release.argo_workflows.values[0]).extraObjects : o if o.metadata.name == "argo-workflows-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-observability"
		error_message="A custom observability_config.namespace must flow into argo-workflows-metrics-ingress"
	}
	command=plan
	variables {
		observability_config={
			namespace="custom-observability"
		}
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