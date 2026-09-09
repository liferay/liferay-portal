mock_provider "google" {
	mock_resource "google_service_account" {
		defaults={
			email="crossplane-gsa@liferay-test-project.iam.gserviceaccount.com"
			name="projects/liferay-test-project/serviceAccounts/crossplane-gsa@liferay-test-project.iam.gserviceaccount.com"
		}
	}
}
mock_provider "helm" {}
mock_provider "kubernetes" {}
override_data {
	target=data.google_project.project
	values={
		number="1234567890"
	}
}
run "should_create_an_http_gateway_for_a_hostname_only" {
	assert {
		condition=kubernetes_manifest.argo_workflows_httproute[0].manifest.spec.parentRefs[0].sectionName == "http"
		error_message="The HTTPRoute must attach to the http listener if TLS is disabled"
	}
	assert {
		condition=length(kubernetes_manifest.argo_workflows_gateway[0].manifest.spec.listeners) == 1
		error_message="The Argo Workflows Gateway must expose only the HTTP listener if TLS is disabled"
	}
	assert {
		condition=length(kubernetes_manifest.argo_workflows_https_redirect) == 0
		error_message="The HTTPS redirect route resources must not be created if TLS is disabled"
	}
	command=plan
	variables {
		argo_workflows_domain_config={
			hostname="argo.example.com"
		}
	}
}
run "should_enable_https_and_prefix_the_tls_secret_name" {
	assert {
		condition=kubernetes_manifest.argo_workflows_server_tls_external_secret[0].manifest.spec.dataFrom[0].extract.key == "liferay-certificates-argo-cert"
		error_message="A bare TLS secret name must be prefixed with liferay-certificates-"
	}
	assert {
		condition=length(kubernetes_manifest.argo_workflows_gateway[0].manifest.spec.listeners) == 2
		error_message="A TLS secret must add the HTTPS listener to the Gateway"
	}
	assert {
		condition=length(kubernetes_manifest.argo_workflows_https_redirect) == 1
		error_message="The HTTP to HTTPS redirect route resources must be created if TLS is enabled"
	}
	command=plan
	variables {
		argo_workflows_domain_config={
			hostname="argo.example.com"
			tls_external_secret_name="argo-cert"
		}
	}
}
run "should_not_create_a_gateway_without_a_hostname" {
	assert {
		condition=length(kubernetes_manifest.argo_workflows_gateway) == 0 && length(kubernetes_manifest.argo_workflows_httproute) == 0
		error_message="The Gateway or HTTPRoute resources must not be created if no hostname is configured"
	}
	command=plan
}
run "should_not_double_prefix_an_already_prefixed_tls_secret" {
	assert {
		condition=kubernetes_manifest.argo_workflows_server_tls_external_secret[0].manifest.spec.dataFrom[0].extract.key == "liferay-certificates-argo-cert"
		error_message="An already prefixed TLS secret name must not be prefixed again"
	}
	command=plan
	variables {
		argo_workflows_domain_config={
			hostname="argo.example.com"
			tls_external_secret_name="liferay-certificates-argo-cert"
		}
	}
}
run "should_route_to_the_argo_workflows_server_in_its_own_namespace" {
	assert {
		condition=kubernetes_manifest.argo_workflows_httproute[0].manifest.spec.rules[0].backendRefs[0].name == "argo-workflows-server"
		error_message="The HTTPRoute must send traffic to the Argo Workflows server service"
	}
	assert {
		condition=kubernetes_manifest.argo_workflows_httproute[0].manifest.spec.rules[0].backendRefs[0].port == 2746
		error_message="The HTTPRoute must target port 2746, which is the port the Argo Workflows server listens on"
	}
	assert {
		condition=kubernetes_manifest.argo_workflows_gateway[0].manifest.metadata.namespace == "argo-workflows-system"
		error_message="The Gateway must live in the Argo Workflows namespace, since its listeners only allow routes from the same namespace"
	}
	command=plan
	variables {
		argo_workflows_domain_config={
			hostname="argo.example.com"
			tls_external_secret_name="argo-cert"
		}
	}
}
variables {
	deployment_name="liferay-test"
	infrastructure_helm_chart_version="0.4.9"
	infrastructure_provider_helm_chart_version="0.3.12"
	liferay_git_repo_url="https://github.com/example/liferay-gitops.git"
	liferay_helm_chart_version="0.4.20"
	observability_helm_chart_version="0.1.0"
	project_id="liferay-test-project"
	region="us-central1"
}