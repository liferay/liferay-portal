mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
run "should_add_every_metrics_rule_when_observability_is_enabled" {
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-application-controller"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "observability"
		error_message="argocd-application-controller must scope its metrics rule to the observability namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-applicationset-controller-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "observability"
		error_message="argocd-applicationset-controller-ingress must scope its metrics rule to the observability namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-notifications-controller-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "observability"
		error_message="argocd-notifications-controller-ingress must scope its metrics rule to the observability namespace"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress) == 2 && [for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "observability"
		error_message="argocd-dex-server must add a metrics rule scoped to the observability namespace after its component rule"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-redis"][0].spec.ingress) == 1
		error_message="argocd-redis must not get a metrics rule, since the redis pod exposes no metrics port unless the exporter sidecar is enabled"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress) == 2 && [for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "observability"
		error_message="argocd-repo-server must add a metrics rule scoped to the observability namespace after its component rule"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress) == 2 && [for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress[1].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "observability"
		error_message="argocd-server-ingress must add a metrics rule scoped to the observability namespace after its component rule"
	}
	command=plan
	variables {
		observability_enabled=true
	}
}
run "should_advertise_the_external_url_without_enabling_sso" {
	assert {
		condition=anytrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm.url, null) == "http://argocd.liferay.test"])
		error_message="The ArgoCD external URL must fall back to HTTP while the gateway terminates no TLS"
	}
	assert {
		condition=alltrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm["dex.config"], null) == null])
		error_message="Publishing an external URL must not enable SSO on its own"
	}
	command=plan
	variables {
		argocd_external_access_config={
			hostname="argocd.liferay.test"
		}
	}
}
run "should_append_the_additional_values_last" {
	assert {
		condition=element(helm_release.argocd.values, length(helm_release.argocd.values) - 1) == "controller:\n  replicas: 2\n"
		error_message="The additional values must land last so callers override the module defaults"
	}
	command=plan
	variables {
		additional_values=[
			"controller:\n  replicas: 2\n",
		]
	}
}
run "should_configure_the_dex_connector_when_sso_is_enabled" {
	assert {
		condition=anytrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm["dex.config"], null) != null])
		error_message="The ArgoCD release must configure the SAML connector once SSO is enabled"
	}
	assert {
		condition=anytrue([for value in helm_release.argocd.values : try(yamldecode(yamldecode(value).configs.cm["dex.config"]).connectors[0].type, null) == "saml"])
		error_message="The Dex connector must declare the SAML type"
	}
	assert {
		condition=anytrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.rbac["policy.default"], null) == "role:liferay-guest"])
		error_message="Enabling SSO must install the Liferay RBAC policy alongside the connector"
	}
	assert {
		condition=anytrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm.url, null) == "https://argocd.liferay.test"])
		error_message="Enabling SSO must still advertise the external URL Dex derives its issuer from"
	}
	command=plan
	variables {
		argocd_external_access_config={
			hostname="argocd.liferay.test"
			sso_enabled=true
			tls_enabled=true
		}
	}
}
run "should_derive_a_tls_external_url" {
	assert {
		condition=anytrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm.url, null) == "https://argocd.liferay.test"])
		error_message="The ArgoCD external URL must use HTTPS once the gateway terminates TLS"
	}
	command=plan
	variables {
		argocd_external_access_config={
			hostname="argocd.liferay.test"
			tls_enabled=true
		}
	}
}
run "should_disable_admin_login_independently_of_external_access" {
	assert {
		condition=!yamldecode(helm_release.argocd.values[0]).configs.cm["admin.enabled"]
		error_message="The admin login toggle must stand on its own, so hardening it does not require SSO"
	}
	command=plan
	variables {
		argocd_admin_login_enabled=false
	}
}
run "should_disable_the_chart_network_policies" {
	assert {
		condition=!yamldecode(helm_release.argocd.values[0]).global.networkPolicy.create
		error_message="The ArgoCD release must turn off the chart's own NetworkPolicies, which argo-cd 10 creates by default, collide by name with the extraObjects ones, and include an allow all argocd-server policy"
	}
	assert {
		condition=alltrue([for component in ["controller", "dex", "redis", "repoServer", "server"] : !contains(keys(yamldecode(helm_release.argocd.values[0])[component]), "networkPolicy")])
		error_message="No component may set networkPolicy.create, so the chart never renders a policy that collides with the extraObjects ones"
	}
	assert {
		condition=!contains(keys(yamldecode(helm_release.argocd.values[0]).applicationSet), "metrics") && !contains(keys(yamldecode(helm_release.argocd.values[0]).notifications), "metrics")
		error_message="The ArgoCD release must not enable the applicationSet or notifications metrics servers just to get their chart policies"
	}
	command=plan
}
run "should_enable_admin_login_by_default" {
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).configs.cm["admin.enabled"]
		error_message="The ArgoCD release must leave the built in admin login enabled by default"
	}
	command=plan
}
run "should_enforce_the_restricted_pod_security_standard" {
	assert {
		condition=kubernetes_namespace_v1.argocd.metadata[0].labels["pod-security.kubernetes.io/enforce"] == "restricted"
		error_message="The ArgoCD namespace must enforce the restricted Pod Security Standard"
	}
	command=plan
}
run "should_keep_the_chart_resource_exclusions" {
	assert {
		condition=alltrue([for kind in ["CertificateSigningRequest", "CiliumEndpoint", "CiliumIdentity", "EndpointSlice", "Endpoints", "Lease", "PolicyReport", "TokenReview"] : anytrue([for exclusion in yamldecode(yamldecode(helm_release.argocd.values[0]).configs.cm["resource.exclusions"]) : contains(exclusion.kinds, kind)])])
		error_message="The resource exclusions must keep the chart's default kinds, since this value replaces the chart's list"
	}
	assert {
		condition=alltrue([for kind in ["ManagedResourceDefinition", "ProviderConfigUsage"] : anytrue([for exclusion in yamldecode(yamldecode(helm_release.argocd.values[0]).configs.cm["resource.exclusions"]) : contains(exclusion.kinds, kind)])])
		error_message="The resource exclusions must add the Crossplane kinds ArgoCD should not track"
	}
	command=plan
}
run "should_omit_every_metrics_rule_when_observability_is_disabled" {
	assert {
		condition=alltrue([for name in ["argocd-application-controller", "argocd-applicationset-controller-ingress", "argocd-notifications-controller-ingress"] : length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == name][0].spec.ingress) == 0])
		error_message="The metrics only policies must have no ingress rule while observability is disabled"
	}
	assert {
		condition=alltrue([for name in ["argocd-dex-server", "argocd-redis", "argocd-repo-server", "argocd-server-ingress"] : length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == name][0].spec.ingress) == 1])
		error_message="The component policies must keep only their component rule while observability is disabled"
	}
	command=plan
}
run "should_omit_the_external_url_and_the_dex_connector_by_default" {
	assert {
		condition=alltrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm.url, null) == null])
		error_message="The ArgoCD release must leave the external URL unset while no caller publishes one"
	}
	assert {
		condition=alltrue([for value in helm_release.argocd.values : try(yamldecode(value).configs.cm["dex.config"], null) == null])
		error_message="The ArgoCD release must omit the SAML connector while ArgoCD is not externally reachable"
	}
	command=plan
}
run "should_register_the_health_checks_under_the_infrastructure_api_group" {
	assert {
		condition=contains(keys(yamldecode(helm_release.argocd.values[0]).configs.cm), "resource.customizations.health.azure.liferay.com_LiferayInfrastructure")
		error_message="The LiferayInfrastructure health check key must carry the caller's infrastructure API group"
	}
	command=plan
}
run "should_restrict_each_component_to_its_callers" {
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress[0].from[0].namespaceSelector.matchLabels["kubernetes.io/metadata.name"] == "envoy-gateway-system"
		error_message="argocd-server must only accept traffic from the Envoy Gateway namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-server-ingress"][0].spec.ingress[0].from[0].podSelector.matchLabels["gateway.envoyproxy.io/owning-gateway-namespace"] == "argocd-system"
		error_message="argocd-server must only accept the Envoy proxies of the Gateway in the ArgoCD namespace"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-dex-server"][0].spec.ingress[0].from[0].podSelector.matchLabels["app.kubernetes.io/name"] == "argocd-server"
		error_message="dex must only accept argocd-server"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-redis"][0].spec.ingress[0].from) == 3
		error_message="redis must accept exactly the application controller, repo server, and server"
	}
	assert {
		condition=length([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "argocd-repo-server"][0].spec.ingress[0].from) == 4
		error_message="The repo server must accept exactly the application controller, ApplicationSet controller, notifications controller, and server"
	}
	assert {
		condition=[for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec.podSelector == {} && !contains(keys([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o if o.metadata.name == "default-deny-ingress"][0].spec), "ingress")
		error_message="The namespace must deny all ingress not allowed by a component policy"
	}
	command=plan
}
run "should_tolerate_the_restore_workflow_by_its_field_manager" {
	assert {
		condition=contains(yamldecode(yamldecode(helm_release.argocd.values[0]).configs.cm["resource.customizations.ignoreDifferences.azure.liferay.com_LiferayInfrastructure"]).managedFieldsManagers, "liferay-backup-restore")
		error_message="The LiferayInfrastructure ignore rule must name the restore workflow's field manager, so every field the restore writes is tolerated"
	}
	assert {
		condition=!contains(keys(yamldecode(yamldecode(helm_release.argocd.values[0]).configs.cm["resource.customizations.ignoreDifferences.azure.liferay.com_LiferayInfrastructure"])), "jsonPointers")
		error_message="The LiferayInfrastructure ignore rule must not enumerate field paths, so growing the restore contract needs no ArgoCD change"
	}
	command=plan
}
run "should_write_the_network_policies_through_extra_objects" {
	assert {
		condition=length(yamldecode(helm_release.argocd.values[0]).extraObjects) == 8 && alltrue([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : o.kind == "NetworkPolicy"])
		error_message="The ArgoCD release must render exactly eight NetworkPolicies through extraObjects"
	}
	assert {
		condition=alltrue([for o in yamldecode(helm_release.argocd.values[0]).extraObjects : !contains(keys(o.metadata), "namespace")])
		error_message="The NetworkPolicies must inherit the release namespace"
	}
	command=plan
}
variables {
	argocd_helm_chart_version="10.1.3"
	infrastructure_api_group="azure.liferay.com"
}