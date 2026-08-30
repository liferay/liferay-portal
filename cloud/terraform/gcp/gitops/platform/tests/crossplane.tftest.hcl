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
run "should_scope_the_manual_network_policies_correctly" {
	assert {
		condition=length(yamldecode(helm_release.crossplane.values[0]).extraObjects) == 4
		error_message="Four extra manifests are expected: function gRPC ingress, metrics ingress, webhook ingress, and the namespace-wide default-deny — the crossplane chart has zero native NetworkPolicy support (confirmed by rendering the chart with every networkPolicy value we could find; none exist), so everything here has to be written by hand"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o.kind == "NetworkPolicy"])
		error_message="Every extraObjects entry must be a NetworkPolicy"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : !contains(keys(o.metadata), "namespace")])
		error_message="Every extraObjects NetworkPolicy must omit metadata.namespace so it inherits the Helm release namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-function-grpc-ingress"][0].spec.podSelector.matchExpressions[0] == { key="pkg.crossplane.io/function", operator="Exists" }
		error_message="crossplane-function-grpc-ingress must select every pod carrying the pkg.crossplane.io/function label (Exists, not a fixed name list) so future Crossplane functions are covered automatically without a code change"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-function-grpc-ingress"][0].spec.ingress[0].from[0].podSelector.matchLabels == { app="crossplane", release="crossplane" }
		error_message="crossplane-function-grpc-ingress must allow only the crossplane core pod (app=crossplane), not crossplane-rbac-manager, which shares the app.kubernetes.io/name=crossplane label but must not reach the function runtimes"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-function-grpc-ingress"][0].spec.ingress[0].ports[0].port == "grpc"
		error_message="crossplane-function-grpc-ingress must scope its allow to the grpc-named port, confirmed via kubectl against a live cluster to be containerPort 9443 on every function runtime pod"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-metrics-ingress"][0].spec.podSelector == {}
		error_message="crossplane-metrics-ingress must apply to every pod in the namespace — core, rbac-manager, every function, and every provider all expose a metrics port, and there is no single label shared by all of them to select more narrowly"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_namespace
		error_message="crossplane-metrics-ingress must allow only the observability namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-metrics-ingress"][0].spec.ingress[0].ports[0].port == "metrics"
		error_message="crossplane-metrics-ingress must scope its allow to the metrics-named port"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-webhook-ingress"][0].spec.podSelector == {}
		error_message="crossplane-webhook-ingress must apply to every pod in the namespace — every Crossplane provider registers its own admission webhook on the same port as crossplane core, confirmed live (6 GCP providers plus provider-kubernetes each expose containerPort 9443 named webhook, distinct from crossplane core's own webhooks port), and providers carry no common label to select them more narrowly, so a blanket podSelector is the only durable option as new providers get added"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-webhook-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == var.master_ipv4_cidr_block
		error_message="crossplane-webhook-ingress must allow only the GKE control plane's CIDR — namespaceSelector/podSelector can never match traffic from outside the cluster, only ipBlock can"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-webhook-ingress"][0].spec.ingress[0].ports[0].port == 9443
		error_message="crossplane-webhook-ingress must reference the webhook port by number, not by name — crossplane core names it 'webhooks' while every provider names it 'webhook' (singular), so no single port name matches both"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec.podSelector == {}
		error_message="default-deny-ingress must have an empty podSelector (matches every pod in the namespace)"
	}
	assert {
		condition=!contains(keys([for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec), "ingress")
		error_message="default-deny-ingress must declare zero ingress rules — any ingress key at all would allow something"
	}
	command=plan
}
run "should_honor_custom_master_cidr_and_observability_namespace" {
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-webhook-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == "10.1.2.0/28"
		error_message="A custom master_ipv4_cidr_block must flow into crossplane-webhook-ingress"
	}
	assert {
		condition=[for o in yamldecode(helm_release.crossplane.values[0]).extraObjects : o if o.metadata.name == "crossplane-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-observability"
		error_message="A custom observability_namespace must flow into crossplane-metrics-ingress"
	}
	command=plan
	variables {
		master_ipv4_cidr_block="10.1.2.0/28"
		observability_namespace="custom-observability"
	}
}
variables {
	argo_workflows_helm_chart_version="1.0.10"
	argocd_helm_chart_version="9.5.16"
	crossplane_helm_chart_version="2.1.3"
	deployment_name="liferay-test"
	external_secrets_helm_chart_version="1.0.0"
	keda_helm_chart_version="2.19.0"
	project_id="liferay-test-project"
	region="us-central1"
}