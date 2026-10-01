mock_provider "google" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_disable_keda_by_default" {
	assert {
		condition=length(helm_release.keda) == 0
		error_message="KEDA must not be installed when keda_enabled is false"
	}
	command=plan
}
run "should_honor_a_custom_keda_namespace" {
	assert {
		condition=helm_release.keda[0].namespace == "autoscaling"
		error_message="A custom keda_namespace must flow to the Helm release"
	}
	command=plan
	variables {
		keda_enabled=true
		keda_namespace="autoscaling"
	}
}
run "should_honor_custom_master_cidr_and_observability_config" {
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-external-metrics-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == "10.1.2.0/28"
		error_message="A custom master_ipv4_cidr_block must flow into keda-external-metrics-ingress"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-external-metrics-ingress"][0].spec.ingress[0].from) == 2
		error_message="keda-external-metrics-ingress must allow two sources: the control plane CIDR and the konnectivity agents"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-external-metrics-ingress"][0].spec.ingress[0].from[1].podSelector.matchLabels["k8s-app"] == "konnectivity-agent"
		error_message="keda-external-metrics-ingress must allow the kube-system konnectivity-agent pods — on GKE the API server reaches in-cluster services through them, so the call arrives from an agent pod IP and never from master_ipv4_cidr_block; the APIService v1beta1.external.metrics.k8s.io is proxied by kube-apiserver through the same tunnel as an admission webhook"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-metrics-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "custom-observability"
		error_message="A custom observability_config.namespace must flow into keda-metrics-ingress"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-webhook-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == "10.1.2.0/28"
		error_message="A custom master_ipv4_cidr_block must flow into keda-webhook-ingress"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-webhook-ingress"][0].spec.ingress[0].from) == 2
		error_message="keda-webhook-ingress must allow two sources: the control plane CIDR and the konnectivity agents"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-webhook-ingress"][0].spec.ingress[0].from[1].podSelector.matchLabels["k8s-app"] == "konnectivity-agent"
		error_message="keda-webhook-ingress must allow the kube-system konnectivity-agent pods — on GKE the API server reaches in-cluster services through them, so the call arrives from an agent pod IP and never from master_ipv4_cidr_block; verified on a live cluster for the sibling components"
	}
	command=plan
	variables {
		keda_enabled=true
		master_ipv4_cidr_block="10.1.2.0/28"
		observability_config={namespace="custom-observability"}
	}
}
run "should_install_keda_when_enabled" {
	assert {
		condition=length(helm_release.keda) == 1
		error_message="KEDA must be installed when keda_enabled is true"
	}
	assert {
		condition=helm_release.keda[0].version == var.keda_helm_chart_version
		error_message="The KEDA Helm release must use the configured chart version"
	}
	assert {
		condition=helm_release.keda[0].create_namespace == true && helm_release.keda[0].namespace == "keda-system"
		error_message="The KEDA Helm release must default to the keda-system namespace and create it"
	}
	command=plan
	variables {
		keda_enabled=true
	}
}
run "should_scope_the_manual_network_policies_correctly" {
	assert {
		condition=length(yamldecode(helm_release.keda[0].values[0]).extraObjects) == 5
		error_message="Five extra manifests are expected: the namespace-wide default-deny, the external metrics ingress, the metrics ingress, the operator's metricsservice ingress, and the admission webhook ingress — the KEDA chart's own NetworkPolicy templates are left disabled, so every policy here has to be written by hand"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o.kind == "NetworkPolicy"])
		error_message="Every extraObjects entry must be a NetworkPolicy"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : !contains(keys(o.metadata), "namespace")])
		error_message="Every extraObjects NetworkPolicy must omit metadata.namespace so it inherits the Helm release namespace"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o.metadata.labels == local.common_labels])
		error_message="Every extraObjects NetworkPolicy must carry only local.common_labels"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec.podSelector == {}
		error_message="default-deny-ingress must have an empty podSelector (matches every pod in the namespace)"
	}
	assert {
		condition=!contains(keys([for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec), "ingress")
		error_message="default-deny-ingress must declare zero ingress rules — any ingress key at all would allow something"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-external-metrics-ingress"][0].spec.podSelector.matchLabels == {"app" = "keda-operator-metrics-apiserver"}
		error_message="keda-external-metrics-ingress must select only the metrics apiserver by its app label, which is the key in the Deployment's own immutable spec.selector; the name label is absent on this workload and app.kubernetes.io/component is shared by all three"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-external-metrics-ingress"][0].spec.ingress[0].ports[0].port == 6443
		error_message="keda-external-metrics-ingress must allow the aggregation layer on the container port 6443, not the Service port 443, because a NetworkPolicy matches the port on the pod rather than on the Service"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-metrics-ingress"][0].spec.podSelector == {}
		error_message="keda-metrics-ingress must apply to every pod in the namespace — only the metrics apiserver declares a metrics container port today, so the rule is inert on the operator and the webhook, and it covers them automatically if prometheus.operator.enabled or prometheus.webhooks.enabled is ever turned on"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-metrics-ingress"][0].spec.ingress[0].ports[0].port == "metrics"
		error_message="keda-metrics-ingress must scope its allow to the metrics-named port"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-metricsservice-ingress"][0].spec.podSelector.matchLabels == {"app" = "keda-operator"}
		error_message="keda-metricsservice-ingress must select only the operator, which is the server for this port"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-metricsservice-ingress"][0].spec.ingress[0].from[0].podSelector.matchLabels == {"app" = "keda-operator-metrics-apiserver"}
		error_message="keda-metricsservice-ingress must allow only the metrics apiserver, which is started with --metrics-service-address pointing at keda-operator:9666; the peer carries no namespaceSelector so the allow stays inside keda-system, unlike the chart's own disabled policy which opens this port to every namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-metricsservice-ingress"][0].spec.ingress[0].ports[0].port == "metricsservice"
		error_message="keda-metricsservice-ingress must scope its allow to the metricsservice-named port, and the default-deny would otherwise break every external metric lookup"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-webhook-ingress"][0].spec.podSelector.matchLabels == {"app" = "keda-admission-webhooks"}
		error_message="keda-webhook-ingress must select only the admission webhook pod"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-webhook-ingress"][0].spec.ingress[0].ports[0].port == 9443
		error_message="keda-webhook-ingress must reference the webhook port by number, not by name — the container port 9443 is named http while the Service in front names the same port https, and a NetworkPolicy resolves the container port name, so https would match nothing; the chart defaults failurePolicy to Ignore, so the dropped calls would admit invalid ScaledObjects with no error anywhere"
	}
	assert {
		condition=yamldecode(helm_release.keda[0].values[0]).networkPolicy.enabled == false
		error_message="The KEDA chart's own NetworkPolicy templates must stay disabled — they allow ingress from an empty namespaceSelector, meaning every namespace in the cluster, on 9666, 6443 and 9443, which would reopen the cross-namespace ingress this set closes, and they would add an Egress policyType the hand-written policies deliberately omit"
	}
	command=plan
	variables {
		keda_enabled=true
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