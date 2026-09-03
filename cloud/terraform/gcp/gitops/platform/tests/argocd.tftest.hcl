mock_provider "google" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_add_every_metrics_rule_when_observability_is_enabled" {
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-application-controller"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argocd-application-controller must scope its metrics rule to the configured observability namespace specifically, not any namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-applicationset-controller-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argocd-applicationset-controller-ingress must scope its metrics rule to the configured observability namespace specifically"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-notifications-controller-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argocd-notifications-controller-ingress must scope its metrics rule to the configured observability namespace specifically"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress) == 2
		error_message="argocd-dex-server must add its metrics rule alongside the argocd-server rule when observability_config.enabled is true"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argocd-dex-server must scope its metrics rule to the configured observability namespace specifically, not any namespace"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-redis"][0].spec.ingress) == 2
		error_message="argocd-redis must add its metrics rule when observability_config.enabled is true"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-redis"][0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argocd-redis must scope its metrics rule to the configured observability namespace specifically, not any namespace"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress) == 2
		error_message="argocd-repo-server must add its metrics rule when observability_config.enabled is true"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="argocd-repo-server must scope its metrics rule to the configured observability namespace specifically, not any namespace"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress) == 2
		error_message="argocd-server-ingress must add its metrics rule alongside the Gateway rule when observability_config.enabled is true"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
			namespace="observability"
		}
	}
}
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
run "should_honor_a_custom_gateway_namespace" {
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-gateway"
		error_message="A custom gateway_namespace must flow into argocd-server-ingress"
	}
	command=plan
	variables {
		gateway_namespace="custom-gateway"
	}
}
run "should_not_touch_chart_level_network_policy_or_metrics_settings" {
	assert {
		condition=!contains(keys(yamldecode(helm_release.argocd.values[0])), "global")
		error_message="global.networkPolicy.create must NOT be used — the argo-cd 9.5.16 chart ORs it with server.networkPolicy.create (see argocd-server/networkpolicy.yaml), so there is no values combination that both enables the chart-wide default-deny and suppresses argocd-server's own allow-all rule"
	}
	assert {
		condition=alltrue([
			for component in ["controller", "dex", "redis", "repoServer", "server"] :
			!contains(keys(yamldecode(helm_release.argocd.values[0])[component]), "networkPolicy")
		])
		error_message="No component should set networkPolicy.create — every NetworkPolicy in this namespace is hand-written via extraObjects instead, using the chart's own rendered rules as reference, so the chart never gets a chance to also render its own (which would collide on name with our extraObjects entry)"
	}
	assert {
		condition=!contains(keys(yamldecode(helm_release.argocd.values[0]).applicationSet), "metrics") && !contains(keys(yamldecode(helm_release.argocd.values[0]).notifications), "metrics")
		error_message="applicationSet.metrics.enabled and notifications.metrics.enabled must stay untouched (chart default is false) — turning either on to get a chart-native NetworkPolicy also turns on the chart's Prometheus metrics server as a side effect. Their ingress is covered by a manual extraObjects policy instead"
	}
	command=plan
}
run "should_omit_every_metrics_rule_when_observability_is_disabled" {
	assert {
		condition=alltrue([
			for name in ["argocd-application-controller", "argocd-applicationset-controller-ingress", "argocd-notifications-controller-ingress"] :
			length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == name][0].spec.ingress) == 0
		])
		error_message="With observability_config.enabled left at its default (false), the metrics-only policies (application-controller, applicationset-controller, notifications-controller) must have zero ingress rules — there is nothing else legitimate reaching them"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress) == 1
		error_message="argocd-dex-server must drop its metrics rule (keeping only the argocd-server rule) when observability_config.enabled is false"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-redis"][0].spec.ingress) == 1
		error_message="argocd-redis must drop its metrics rule when observability_config.enabled is false"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress) == 1
		error_message="argocd-repo-server must drop its metrics rule when observability_config.enabled is false"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress) == 1
		error_message="argocd-server-ingress must drop its metrics rule (keeping only the Gateway rule) when observability_config.enabled is false"
	}
	command=plan
}
run "should_require_explicit_gateway_and_pod_selectors_regardless_of_observability" {
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.gateway_namespace
		error_message="argocd-server-ingress must always allow the configured gateway namespace, regardless of observability_config.enabled"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress[0].from[0].podSelector.matchLabels["gateway.envoyproxy.io/owning-gateway-namespace"] == var.argocd_namespace
		error_message="argocd-server-ingress's Gateway rule must scope to the Envoy proxy that owns this namespace's Gateway, not any proxy in envoy-gateway-system"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress[0].from[0].podSelector.matchLabels["app.kubernetes.io/name"] == "argocd-server"
		error_message="argocd-dex-server must always allow argocd-server on http/grpc, regardless of observability_config.enabled"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-redis"][0].spec.ingress[0].from) == 3
		error_message="argocd-redis must always allow server, repo-server, and application-controller on the redis port, regardless of observability_config.enabled"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress[0].from) == 4
		error_message="argocd-repo-server must always allow server, controller, notifications, and applicationset on the repo-server port, regardless of observability_config.enabled"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec.podSelector == {}
		error_message="default-deny-ingress must have an empty podSelector (matches every pod in the namespace)"
	}
	assert {
		condition=!contains(keys([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec), "ingress")
		error_message="default-deny-ingress must declare zero ingress rules — any ingress key at all would allow something"
	}
	command=plan
}
run "should_write_all_eight_network_policies_by_hand_via_extra_objects" {
	assert {
		condition=length(yamldecode(helm_release.argocd.values[0]).extraObjects) == 8
		error_message="Eight extra manifests are expected: one per argo-cd component (application-controller, applicationset-controller, dex-server, notifications-controller, redis, repo-server, server) plus the namespace-wide default-deny"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o.kind == "NetworkPolicy"])
		error_message="Every extraObjects entry must be a NetworkPolicy"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : !contains(keys(o.metadata), "namespace")])
		error_message="Every extraObjects NetworkPolicy must omit metadata.namespace so it inherits the Helm release namespace"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : !contains(keys(o.metadata.labels), "app.kubernetes.io/name")])
		error_message="metadata.labels on a NetworkPolicy must not carry app.kubernetes.io/name — it is irrelevant for this kind of resource, per review feedback on LCD-52972; only local.common_labels should be present"
	}
	command=plan
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