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
run "should_enable_per_component_network_policies_without_the_global_toggle" {
	assert {
		condition=!contains(keys(yamldecode(helm_release.argocd.values[0])), "global")
		error_message="global.networkPolicy.create must NOT be used — the argo-cd 9.5.16 chart ORs it with server.networkPolicy.create (see argocd-server/networkpolicy.yaml), so there is no values combination that both enables the chart-wide default-deny and suppresses argocd-server's own allow-all rule. Each component must be toggled individually instead"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).controller.networkPolicy.create == true
		error_message="controller.networkPolicy.create must be true so the chart renders its scoped NetworkPolicy"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).repoServer.networkPolicy.create == true
		error_message="repoServer.networkPolicy.create must be true so the chart renders its scoped NetworkPolicy"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).redis.networkPolicy.create == true
		error_message="redis.networkPolicy.create must be true so the chart renders its scoped NetworkPolicy"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).dex.networkPolicy.create == true
		error_message="dex.networkPolicy.create must be true so the chart renders its scoped NetworkPolicy"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).applicationSet.networkPolicy.create == true && yamldecode(helm_release.argocd.values[0]).applicationSet.metrics.enabled == true
		error_message="applicationSet needs both networkPolicy.create and metrics.enabled — the chart's template ANDs (create OR global.create) with (metrics.enabled OR ingress.enabled), so metrics.enabled alone or networkPolicy.create alone is not enough"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).notifications.networkPolicy.create == true && yamldecode(helm_release.argocd.values[0]).notifications.metrics.enabled == true
		error_message="notifications needs both networkPolicy.create and metrics.enabled — same AND condition as applicationSet"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).server.networkPolicy.create == false
		error_message="server.networkPolicy.create must stay false — the chart's own argocd-server NetworkPolicy is unconditionally ingress: [{}] (allow from anywhere); the manual extraObjects policy is the only thing that should govern this pod's ingress"
	}
	command=plan
}
run "should_scope_the_manual_argocd_server_networkpolicy_correctly" {
	assert {
		condition=length(yamldecode(helm_release.argocd.values[0]).extraObjects) == 2
		error_message="Two extra manifests are expected: the scoped argocd-server policy and the namespace-wide default-deny that replaces the chart's own (unusable) global.defaultDenyIngress"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].kind == "NetworkPolicy"
		error_message="The first extraObjects entry must be a NetworkPolicy"
	}
	assert {
		condition=!contains(keys(yamldecode(helm_release.argocd.values[0]).extraObjects[0].metadata), "namespace")
		error_message="The extraObjects NetworkPolicy must omit metadata.namespace so it inherits the Helm release namespace"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].spec.podSelector.matchLabels["app.kubernetes.io/name"] == "argocd-server"
		error_message="The first NetworkPolicy must select the argocd-server pods"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.gateway_namespace
		error_message="The first ingress rule must allow only the configured gateway namespace"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].spec.ingress[0].from[0].podSelector.matchLabels["gateway.envoyproxy.io/owning-gateway-namespace"] == var.argocd_namespace
		error_message="The Gateway ingress rule must scope to the Envoy proxy that owns this namespace's Gateway, not any proxy in envoy-gateway-system"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_namespace
		error_message="The second ingress rule must allow only the observability namespace for metrics scraping"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[1].kind == "NetworkPolicy" && yamldecode(helm_release.argocd.values[0]).extraObjects[1].spec.podSelector == {}
		error_message="The second extraObjects entry must be a namespace-wide default-deny (empty podSelector matches every pod in the namespace)"
	}
	assert {
		condition=!contains(keys(yamldecode(helm_release.argocd.values[0]).extraObjects[1].spec), "ingress")
		error_message="The default-deny policy must declare zero ingress rules — any ingress key at all would allow something"
	}
	command=plan
}
run "should_honor_custom_gateway_and_observability_namespaces" {
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-gateway"
		error_message="A custom gateway_namespace must flow into the argocd-server NetworkPolicy"
	}
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).extraObjects[0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-observability"
		error_message="A custom observability_namespace must flow into the argocd-server NetworkPolicy"
	}
	command=plan
	variables {
		gateway_namespace="custom-gateway"
		observability_namespace="custom-observability"
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