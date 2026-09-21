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
run "should_honor_custom_master_cidr_and_observability_config" {
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-observability"
		error_message="A custom observability_config.namespace must flow into external-secrets-metrics-ingress"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-webhook-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == "10.1.2.0/28"
		error_message="A custom master_ipv4_cidr_block must flow into external-secrets-webhook-ingress"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-webhook-ingress"][0].spec.ingress[0].from) == 2
		error_message="external-secrets-webhook-ingress must allow two sources: the control plane CIDR and the konnectivity agents"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-webhook-ingress"][0].spec.ingress[0].from[1].podSelector.matchLabels["k8s-app"] == "konnectivity-agent"
		error_message="external-secrets-webhook-ingress must allow the kube-system konnectivity-agent pods — on GKE the API server reaches in-cluster webhooks through them, so the call arrives from an agent pod IP and never from master_ipv4_cidr_block; verified on a live cluster"
	}
	command=plan
	variables {
		master_ipv4_cidr_block="10.1.2.0/28"
		observability_config={namespace="custom-observability"}
	}
}
run "should_scope_the_manual_network_policies_correctly" {
	assert {
		condition=length(yamldecode(helm_release.external_secrets.values[0]).extraObjects) == 3
		error_message="Three extra manifests are expected: the metrics ingress, the webhook ingress, and the namespace-wide default-deny — the external-secrets chart ships no NetworkPolicy of its own, so each one has to be written by hand"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o.kind == "NetworkPolicy"])
		error_message="Every extraObjects entry must be a NetworkPolicy"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : !contains(keys(o.metadata), "namespace")])
		error_message="Every extraObjects NetworkPolicy must omit metadata.namespace so it inherits the Helm release namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec.podSelector == {}
		error_message="default-deny-ingress must have an empty podSelector (matches every pod in the namespace)"
	}
	assert {
		condition=!contains(keys([for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec), "ingress")
		error_message="default-deny-ingress must declare zero ingress rules — any ingress key at all would allow something"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-metrics-ingress"][0].spec.podSelector == {}
		error_message="external-secrets-metrics-ingress must apply to every pod in the namespace — the controller, the webhook and the cert-controller each expose a metrics port, and they share no single label that selects all three and nothing else"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == var.observability_config.namespace
		error_message="external-secrets-metrics-ingress must allow only the observability namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-metrics-ingress"][0].spec.ingress[0].ports[0].port == "metrics"
		error_message="external-secrets-metrics-ingress must scope its allow to the metrics-named port"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-webhook-ingress"][0].spec.podSelector.matchLabels == {"app.kubernetes.io/instance" = "external-secrets", "app.kubernetes.io/name" = "external-secrets-webhook"}
		error_message="external-secrets-webhook-ingress must select only the webhook pod — it is the sole component the API server calls, so the controller and the cert-controller stay unreachable"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-webhook-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == var.master_ipv4_cidr_block
		error_message="external-secrets-webhook-ingress must keep the GKE control plane's CIDR as its first source — ipBlock is the only selector that can match traffic from outside the cluster, but on GKE it is not sufficient alone, which is what the konnectivity source covers"
	}
	assert {
		condition=[for o in yamldecode(helm_release.external_secrets.values[0]).extraObjects : o if o.metadata.name == "external-secrets-webhook-ingress"][0].spec.ingress[0].ports[0].port == "webhook"
		error_message="external-secrets-webhook-ingress must scope its allow to the webhook-named container port, not the 443 Service port, because a NetworkPolicy matches the port on the pod rather than on the Service"
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