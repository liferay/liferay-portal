mock_provider "google" {}
mock_provider "google-beta" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "null" {}
mock_provider "time" {}
override_data {
	target=data.google_compute_zones.available
	values={
		names=["us-central1-a", "us-central1-b", "us-central1-c"]
	}
}
override_data {
	target=data.google_netblock_ip_ranges.health_checkers
	values={
		cidr_blocks_ipv4=["35.191.0.0/16"]
	}
}
override_data {
	target=data.google_netblock_ip_ranges.legacy_health_checkers
	values={
		cidr_blocks_ipv4=["130.211.0.0/22"]
	}
}
override_data {
	target=data.google_project.project
	values={
		number="1234567890"
	}
}
run "should_configure_the_envoy_gateway_helm_release" {
	assert {
		condition=helm_release.envoy_gateway.chart == "gateway-helm"
		error_message="The Envoy Gateway release must use the gateway-helm chart"
	}
	assert {
		condition=helm_release.envoy_gateway.namespace == "envoy-gateway-system"
		error_message="The Envoy Gateway release must default to the envoy-gateway-system namespace"
	}
	assert {
		condition=helm_release.envoy_gateway.version == "v1.6.3"
		error_message="The Envoy Gateway chart version must be \"v${var.envoy_gateway_helm_chart_version}\""
	}
	command=plan
}
run "should_create_the_envoy_proxy_pod_disruption_budget" {
	assert {
		condition=kubernetes_pod_disruption_budget_v1.envoy_proxy_pdb.spec[0].max_unavailable == "1"
		error_message="The Envoy proxy PDB must allow at most 1 unavailable pod"
	}
	assert {
		condition=kubernetes_pod_disruption_budget_v1.envoy_proxy_pdb.spec[0].selector[0].match_labels["app.kubernetes.io/name"] == "envoy"
		error_message="The Envoy proxy PDB must select Envoy proxy pods"
	}
	command=plan
}
run "should_honor_a_custom_gateway_namespace" {
	assert {
		condition=helm_release.envoy_gateway.namespace == "custom-gw"
		error_message="The Envoy Gateway release must honor a custom gateway_namespace"
	}
	assert {
		condition=kubernetes_pod_disruption_budget_v1.envoy_proxy_pdb.metadata[0].namespace == "custom-gw"
		error_message="The Envoy proxy PDB must be created in the configured gateway_namespace"
	}
	assert {
		condition=kubernetes_network_policy_v1.default_deny_ingress.metadata[0].namespace == "custom-gw"
		error_message="default-deny-ingress must be created in the configured gateway_namespace"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_ingress.metadata[0].namespace == "custom-gw"
		error_message="envoy-gateway-ingress must be created in the configured gateway_namespace"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_metrics_ingress.metadata[0].namespace == "custom-gw"
		error_message="envoy-gateway-metrics-ingress must be created in the configured gateway_namespace"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_proxy_ingress.metadata[0].namespace == "custom-gw"
		error_message="envoy-proxy-ingress must be created in the configured gateway_namespace"
	}
	command=plan
	variables {
		gateway_namespace="custom-gw"
	}
}
run "should_honor_a_custom_metrics_scrape_namespace" {
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_metrics_ingress.spec[0].ingress[0].from[0].namespace_selector[0].match_labels["kubernetes.io/metadata.name"] == "custom-scrape"
		error_message="envoy-gateway-metrics-ingress must honor a custom metrics_scrape_config.namespace"
	}
	command=plan
	variables {
		metrics_scrape_config={
			namespace="custom-scrape"
		}
	}
}
run "should_omit_the_metrics_rule_when_scraping_is_disabled" {
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_gateway_metrics_ingress.spec[0].ingress) == 0
		error_message="envoy-gateway-metrics-ingress must declare no ingress rule when scraping is disabled"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_metrics_ingress.metadata[0].name == "envoy-gateway-metrics-ingress"
		error_message="envoy-gateway-metrics-ingress must still exist when scraping is disabled, so enabling it adds a rule rather than a resource"
	}
	command=plan
	variables {
		metrics_scrape_config={
			enabled=false
		}
	}
}
run "should_scope_the_envoy_gateway_network_policies_correctly" {
	assert {
		condition=length(kubernetes_network_policy_v1.default_deny_ingress.spec[0].ingress) == 0
		error_message="default-deny-ingress must declare zero ingress rules — any rule at all would allow something"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.default_deny_ingress.spec[0].policy_types) == 1 && kubernetes_network_policy_v1.default_deny_ingress.spec[0].policy_types[0] == "Ingress"
		error_message="default-deny-ingress must constrain Ingress only, never Egress"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].pod_selector[0].match_labels["app.kubernetes.io/name"] == "gateway-helm"
		error_message="envoy-gateway-ingress must select the control plane by app.kubernetes.io/name=gateway-helm, not envoy-gateway"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress) == 2
		error_message="envoy-gateway-ingress must carry two rules, the webhook and xDS"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[0].from) == 2
		error_message="envoy-gateway-ingress must allow two sources on the webhook port: the control plane CIDR and the konnectivity agents"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[0].from[1].pod_selector[0].match_labels["k8s-app"] == "konnectivity-agent"
		error_message="envoy-gateway-ingress must allow the konnectivity-agent pods; on GKE the API server reaches webhooks through them, not the master CIDR"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[0].from[1].namespace_selector[0].match_labels["kubernetes.io/metadata.name"] == "kube-system"
		error_message="envoy-gateway-ingress must name kube-system on the konnectivity peer, or the peer matches nothing"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[0].ports[0].port == "webhook"
		error_message="envoy-gateway-ingress must allow the webhook by its container port name"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].from[0].pod_selector[0].match_labels) == 2 && kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].from[0].pod_selector[0].match_labels["app.kubernetes.io/managed-by"] == "envoy-gateway" && kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].from[0].pod_selector[0].match_labels["app.kubernetes.io/name"] == "envoy"
		error_message="envoy-gateway-ingress must select the proxies by managed-by and name only, never owning-gateway-name"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].ports) == 3
		error_message="envoy-gateway-ingress must allow three ports on the xDS rule"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].ports[0].port == "grpc" && kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].ports[1].port == "ratelimit" && kubernetes_network_policy_v1.envoy_gateway_ingress.spec[0].ingress[1].ports[2].port == "wasm"
		error_message="envoy-gateway-ingress must name the xDS ports grpc, ratelimit and wasm"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_metrics_ingress.spec[0].ingress[0].from[0].namespace_selector[0].match_labels["kubernetes.io/metadata.name"] == "gmp-system"
		error_message="envoy-gateway-metrics-ingress must default to gmp-system, where GKE runs the managed collection"
	}
	assert {
		condition=kubernetes_network_policy_v1.envoy_gateway_metrics_ingress.spec[0].ingress[0].ports[0].port == "metrics"
		error_message="envoy-gateway-metrics-ingress must scope its allow to the metrics port by name"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].ingress[0].from) == 0
		error_message="envoy-proxy-ingress must declare no from peers; an ipBlock of 0.0.0.0/0 is IPv4 only and misses in-cluster pods"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].ingress[0].ports) == 2 && kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].ingress[0].ports[0].port == "10080" && kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].ingress[0].ports[1].port == "10443"
		error_message="envoy-proxy-ingress must admit only the Envoy listener ports; this policy, not the firewall, drops a listener missing from envoy_listener_ports"
	}
	assert {
		condition=length(kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].pod_selector[0].match_labels) == 2 && kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].pod_selector[0].match_labels["app.kubernetes.io/managed-by"] == "envoy-gateway" && kubernetes_network_policy_v1.envoy_proxy_ingress.spec[0].pod_selector[0].match_labels["app.kubernetes.io/name"] == "envoy"
		error_message="envoy-proxy-ingress must select the proxies by the two stable labels only"
	}
	command=plan
}
run "should_set_the_envoy_gateway_helm_values" {
	assert {
		condition=yamldecode(helm_release.envoy_gateway.values[0]).config.envoyGateway.extensionApis.enableBackend == false
		error_message="The Envoy Gateway backend extension API must be disabled"
	}
	assert {
		condition=yamldecode(helm_release.envoy_gateway.values[0]).deployment.replicas == 2
		error_message="The Envoy Gateway deployment must run 2 replicas"
	}
	assert {
		condition=yamldecode(helm_release.envoy_gateway.values[0]).podDisruptionBudget.maxUnavailable == 1
		error_message="The Envoy Gateway pod disruption budget must allow at most 1 unavailable pod"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	envoy_gateway_helm_chart_version="1.6.3"
	project_id="liferay-test-project"
	region="us-central1"
}