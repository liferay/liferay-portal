mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
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
run "should_enable_admin_login_by_default" {
	assert {
		condition=yamldecode(helm_release.argocd.values[0]).configs.cm["admin.enabled"]
		error_message="The ArgoCD release must leave the built in admin login enabled by default"
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
variables {
	argocd_helm_chart_version="10.1.3"
	infrastructure_api_group="azure.liferay.com"
}