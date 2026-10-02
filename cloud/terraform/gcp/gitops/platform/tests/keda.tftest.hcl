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
run "should_honor_a_custom_master_cidr" {
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-admission-webhooks-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == "10.1.2.0/28"
		error_message="A custom master_ipv4_cidr_block must flow into keda-admission-webhooks-ingress"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-admission-webhooks-ingress"][0].spec.ingress[0].from == local.webhook_ingress_from
		error_message="keda-admission-webhooks-ingress must allow exactly local.webhook_ingress_from — asserting only the podSelector would miss a dropped kube-system namespaceSelector, which would leave the peer matching konnectivity-agent pods inside keda-system, of which there are none, so the API server would be blocked while every assertion still passed"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-metrics-apiserver-ingress"][0].spec.ingress[0].from[0].ipBlock.cidr == "10.1.2.0/28"
		error_message="A custom master_ipv4_cidr_block must flow into keda-operator-metrics-apiserver-ingress"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-metrics-apiserver-ingress"][0].spec.ingress[0].from == local.webhook_ingress_from
		error_message="keda-operator-metrics-apiserver-ingress must allow exactly local.webhook_ingress_from — asserting only the podSelector would miss a dropped kube-system namespaceSelector, which would leave the peer matching konnectivity-agent pods inside keda-system, of which there are none, so the API server would be blocked while every assertion still passed"
	}
	command=plan
	variables {
		keda_enabled=true
		master_ipv4_cidr_block="10.1.2.0/28"
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
		condition=length(yamldecode(helm_release.keda[0].values[0]).extraObjects) == 4
		error_message="Four extra manifests are expected, in this order: default-deny-ingress, keda-admission-webhooks-ingress, keda-operator-ingress, keda-operator-metrics-apiserver-ingress — the KEDA chart's own NetworkPolicy templates are left disabled, so every policy here has to be written by hand"
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
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-admission-webhooks-ingress"][0].spec.podSelector.matchLabels == {"app" = "keda-admission-webhooks"}
		error_message="keda-admission-webhooks-ingress must select only the admission webhook pod"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-admission-webhooks-ingress"][0].spec.ingress[0].ports[0].port == 9443
		error_message="keda-admission-webhooks-ingress must reference the webhook port by number, not by name — the container port 9443 is named http while the Service in front names the same port https, and a NetworkPolicy resolves the container port name, so https would match nothing; the chart defaults failurePolicy to Ignore, so the dropped calls would admit invalid ScaledObjects with no error anywhere; webhooks.port is pinned in the same values so a chart upgrade cannot move it"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-admission-webhooks-ingress"][0].spec.ingress[0].from[1].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "kube-system"
		error_message="keda-admission-webhooks-ingress must name kube-system on the konnectivity peer as a literal. Comparing the whole list to local.webhook_ingress_from cannot catch this: it checks the rendered value against the local that produced it, so editing the local moves both sides and the assertion still passes. Dropping this selector would leave the peer matching konnectivity-agent pods inside keda-system, of which there are none, and the API server would be blocked silently"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-admission-webhooks-ingress"][0].spec.ingress[0].from[1].podSelector.matchLabels["k8s-app"] == "konnectivity-agent"
		error_message="keda-admission-webhooks-ingress must name the konnectivity-agent pods as a literal, for the same reason the namespaceSelector above is asserted literally"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-ingress"][0].spec.podSelector.matchLabels == {"app" = "keda-operator"}
		error_message="keda-operator-ingress must select only the operator, which is the server for this port"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-ingress"][0].spec.ingress[0].from[0].podSelector.matchLabels == {"app" = "keda-operator-metrics-apiserver"}
		error_message="keda-operator-ingress must allow only the metrics apiserver, which is started with --metrics-service-address pointing at keda-operator:9666; the peer carries no namespaceSelector so the allow stays inside keda-system, unlike the chart's own disabled policy which opens this port to every namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-ingress"][0].spec.ingress[0].ports[0].port == "metricsservice"
		error_message="keda-operator-ingress must scope its allow to the metricsservice-named port, and the default-deny would otherwise break every external metric lookup"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-metrics-apiserver-ingress"][0].spec.podSelector.matchLabels == {"app" = "keda-operator-metrics-apiserver"}
		error_message="keda-operator-metrics-apiserver-ingress must select only the metrics apiserver by its app label, which is the key in the Deployment's own immutable spec.selector; the name label is absent on this workload and app.kubernetes.io/component is shared by all three"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-metrics-apiserver-ingress"][0].spec.ingress[0].ports[0].port == 6443
		error_message="keda-operator-metrics-apiserver-ingress must allow the aggregation layer on the container port 6443, not the Service port 443, because a NetworkPolicy matches the port on the pod rather than on the Service; service.portHttpsTarget is pinned in the same values so the chart cannot move it out from under this rule"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-metrics-apiserver-ingress"][0].spec.ingress[0].from[1].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "kube-system"
		error_message="keda-operator-metrics-apiserver-ingress must name kube-system on the konnectivity peer as a literal. Comparing the whole list to local.webhook_ingress_from cannot catch this: it checks the rendered value against the local that produced it, so editing the local moves both sides and the assertion still passes. Dropping this selector would leave the peer matching konnectivity-agent pods inside keda-system, of which there are none, and the API server would be blocked silently"
	}
	assert {
		condition=[for o in yamldecode(helm_release.keda[0].values[0]).extraObjects : o if o.metadata.name == "keda-operator-metrics-apiserver-ingress"][0].spec.ingress[0].from[1].podSelector.matchLabels["k8s-app"] == "konnectivity-agent"
		error_message="keda-operator-metrics-apiserver-ingress must name the konnectivity-agent pods as a literal, for the same reason the namespaceSelector above is asserted literally"
	}
	assert {
		condition=yamldecode(helm_release.keda[0].values[0]).networkPolicy.enabled == false
		error_message="The KEDA chart's own NetworkPolicy templates must stay disabled — they allow ingress from an empty namespaceSelector, meaning every namespace in the cluster, on 9666, 6443 and 9443, which would reopen the cross-namespace ingress this set closes, and they would add an Egress policyType the hand-written policies deliberately omit"
	}
	assert {
		condition=yamldecode(helm_release.keda[0].values[0]).service.portHttpsTarget == 6443
		error_message="service.portHttpsTarget must be pinned to 6443 so the metrics apiserver container port cannot drift away from keda-operator-metrics-apiserver-ingress on a chart upgrade"
	}
	assert {
		condition=yamldecode(helm_release.keda[0].values[0]).webhooks.port == 9443
		error_message="webhooks.port must be pinned to 9443 so the admission webhook container port cannot drift away from keda-admission-webhooks-ingress on a chart upgrade; the chart leaves it empty by default and relies on the binary's default"
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