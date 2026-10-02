resource "helm_release" "envoy_gateway" {
	chart="gateway-helm"
	create_namespace=true
	depends_on=[
		null_resource.wait_for_connect_gateway,
	]
	name="envoy-gateway"
	namespace=var.gateway_namespace
	repository="oci://docker.io/envoyproxy"
	values=[
		yamlencode(
			{
				config={
					envoyGateway={
						extensionApis={
							enableBackend=false
						}
					}
				}
				deployment={
					replicas=2
				}
				podDisruptionBudget={
					maxUnavailable=1
				}
			}),
	]
	version="v${var.envoy_gateway_helm_chart_version}"
}
resource "kubernetes_network_policy_v1" "default_deny_ingress" {
	depends_on=[
		helm_release.envoy_gateway,
		kubernetes_network_policy_v1.envoy_gateway_ingress,
		kubernetes_network_policy_v1.envoy_gateway_metrics_ingress,
		kubernetes_network_policy_v1.envoy_proxy_ingress,
	]
	metadata {
		name="default-deny-ingress"
		namespace=var.gateway_namespace
	}
	spec {
		pod_selector {
		}
		policy_types=["Ingress",]
	}
}
resource "kubernetes_network_policy_v1" "envoy_gateway_ingress" {
	depends_on=[
		helm_release.envoy_gateway,
	]
	metadata {
		name="envoy-gateway-ingress"
		namespace=var.gateway_namespace
	}
	spec {
		ingress {
			from {
				ip_block {
					cidr=var.master_ipv4_cidr_block
				}
			}
			from {
				namespace_selector {
					match_labels={
						"kubernetes.io/metadata.name"="kube-system"
					}
				}
				pod_selector {
					match_labels={
						"k8s-app"="konnectivity-agent"
					}
				}
			}
			ports {
				port="9443"
				protocol="TCP"
			}
		}
		ingress {
			from {
				pod_selector {
					match_labels={
						"app.kubernetes.io/managed-by"="envoy-gateway"
						"app.kubernetes.io/name"="envoy"
					}
				}
			}
			ports {
				port="18000"
				protocol="TCP"
			}
			ports {
				port="18001"
				protocol="TCP"
			}
			ports {
				port="18002"
				protocol="TCP"
			}
		}
		pod_selector {
			match_labels={
				"app.kubernetes.io/instance"="envoy-gateway"
				"app.kubernetes.io/name"="gateway-helm"
				"control-plane"="envoy-gateway"
			}
		}
		policy_types=["Ingress",]
	}
}
resource "kubernetes_network_policy_v1" "envoy_gateway_metrics_ingress" {
	depends_on=[
		helm_release.envoy_gateway,
	]
	metadata {
		name="envoy-gateway-metrics-ingress"
		namespace=var.gateway_namespace
	}
	spec {
		ingress {
			from {
				namespace_selector {
					match_labels={
						"kubernetes.io/metadata.name"="gmp-system"
					}
				}
			}
			ports {
				port="19001"
				protocol="TCP"
			}
		}
		pod_selector {
		}
		policy_types=["Ingress",]
	}
}
resource "kubernetes_network_policy_v1" "envoy_proxy_ingress" {
	depends_on=[
		helm_release.envoy_gateway,
	]
	metadata {
		name="envoy-proxy-ingress"
		namespace=var.gateway_namespace
	}
	spec {
		ingress {
			from {
				ip_block {
					cidr="0.0.0.0/0"
				}
			}
		}
		pod_selector {
			match_labels={
				"app.kubernetes.io/managed-by"="envoy-gateway"
				"app.kubernetes.io/name"="envoy"
			}
		}
		policy_types=["Ingress",]
	}
}
resource "kubernetes_pod_disruption_budget_v1" "envoy_proxy_pdb" {
	depends_on=[
		helm_release.envoy_gateway,
	]
	metadata {
		name="envoy-proxy-pdb"
		namespace=var.gateway_namespace
	}
	spec {
		max_unavailable="1"
		selector {
			match_labels={
				"app.kubernetes.io/component"="proxy"
				"app.kubernetes.io/name"="envoy"
			}
		}
	}
}
resource "null_resource" "wait_for_connect_gateway" {
	depends_on=[
		google_container_cluster.primary,
		google_gke_hub_membership.membership,
	]
	provisioner "local-exec" {
		command="${path.module}/scripts/wait-for-connect-gateway.sh"
		environment={
			GATEWAY_TOKEN="Bearer ${data.google_client_config.default.access_token}"
			GATEWAY_URL="https://connectgateway.googleapis.com/v1/projects/${data.google_project.project.number}/locations/global/gkeMemberships/${var.deployment_name}-membership/api/v1/namespaces"
		}
	}
	triggers={
		membership_id=google_gke_hub_membership.membership.id
	}
}
