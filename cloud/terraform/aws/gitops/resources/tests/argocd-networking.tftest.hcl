mock_provider "aws" {
	mock_data "aws_iam_policy_document" {
		defaults={
			json="{\"Statement\": [], \"Version\": \"2012-10-17\"}"
		}
	}
	mock_resource "aws_iam_policy" {
		defaults={
			arn="arn:aws:iam::123456789012:policy/mock"
		}
	}
}
mock_provider "helm" {}
mock_provider "kubernetes" {}
override_data {
	target=data.aws_caller_identity.current
	values={
		account_id="123456789012"
	}
}
override_data {
	target=data.aws_eks_cluster.cluster
	values={
		identity=[{
			oidc=[{
				issuer="https://oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
			}]
		}]
		vpc_config=[{
			cluster_security_group_id="sg-0123456789abcdef0"
			endpoint_private_access=true
			endpoint_public_access=true
			public_access_cidrs=["0.0.0.0/0"]
			security_group_ids=[]
			subnet_ids=["subnet-aaa", "subnet-bbb"]
			vpc_id="vpc-0123456789abcdef0"
		}]
	}
}
override_data {
	target=data.aws_iam_role.envoy_proxy_role
	values={
		arn="arn:aws:iam::123456789012:role/liferay-test-envoy-proxy"
	}
}
override_data {
	target=data.aws_iam_role.liferay_irsa
	values={
		arn="arn:aws:iam::123456789012:role/liferay-test-irsa"
		id="liferay-test-irsa"
	}
}
override_data {
	target=data.aws_subnets.private
	values={
		ids=["subnet-aaa", "subnet-bbb"]
	}
}
override_data {
	target=data.aws_vpc.current
	values={
		cidr_block="10.0.0.0/16"
	}
}
run "should_create_an_http_gateway_for_a_hostname_only" {
	assert {
		condition=kubernetes_manifest.argocd_httproute[0].manifest.spec.parentRefs[0].sectionName == "http"
		error_message="The HTTPRoute must attach to the http listener if TLS is disabled"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_gateway) == 1
		error_message="The ArgoCD Gateway must be created when a hostname is set"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_gateway[0].manifest.spec.listeners) == 1
		error_message="The ArgoCD Gateway must expose only the HTTP listener if TLS is disabled"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_https_redirect) == 0 && length(kubernetes_manifest.argocd_server_tls_external_secret) == 0
		error_message="The HTTPS redirect or TLS ExternalSecret resources must not be created if TLS is disabled"
	}
	command=plan
	variables {
		argocd_domain_config={
			hostname="argocd.example.com"
		}
	}
}
run "should_enable_https_and_prefix_the_tls_secret_name" {
	assert {
		condition=kubernetes_manifest.argocd_server_tls_external_secret[0].manifest.spec.dataFrom[0].extract.key == "liferay/certificates/argocd-cert"
		error_message="A bare TLS secret name must be prefixed with liferay/certificates/"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_gateway[0].manifest.spec.listeners) == 2
		error_message="A TLS secret must add the HTTPS listener to the Gateway"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_https_redirect) == 1
		error_message="The HTTP to HTTPS redirect route resources must be created if TLS is enabled"
	}
	command=plan
	variables {
		argocd_domain_config={
			hostname="argocd.example.com"
			tls_external_secret_name="argocd-cert"
		}
	}
}
run "should_extend_source_ranges_with_additional_cidrs" {
	assert {
		condition=kubernetes_manifest.argocd_gateway_proxy_config.manifest.spec.provider.kubernetes.envoyService.annotations["service.beta.kubernetes.io/load-balancer-source-ranges"] == "10.0.0.0/16,192.168.0.0/24"
		error_message="The argocd_source_ranges local must append additional allowed CIDR blocks to the VPC CIDR"
	}
	command=plan
	variables {
		argocd_additional_allowed_cidr_blocks=["192.168.0.0/24"]
	}
}
run "should_not_accept_an_invalid_argocd_cidr" {
	command=plan
	expect_failures=[var.argocd_additional_allowed_cidr_blocks]
	variables {
		argocd_additional_allowed_cidr_blocks=["not-a-cidr"]
	}
}
run "should_not_create_a_gateway_without_a_hostname" {
	assert {
		condition=kubernetes_manifest.argocd_gateway_proxy_config.manifest.spec.provider.kubernetes.envoyService.annotations["service.beta.kubernetes.io/load-balancer-source-ranges"] == "10.0.0.0/16"
		error_message="The default load balancer source ranges must be the VPC CIDR only"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_gateway) == 0 && length(kubernetes_manifest.argocd_httproute) == 0
		error_message="The Gateway or HTTPRoute resources must not be created if no hostname is configured"
	}
	command=plan
}
run "should_not_double_prefix_an_already_prefixed_tls_secret" {
	assert {
		condition=kubernetes_manifest.argocd_server_tls_external_secret[0].manifest.spec.dataFrom[0].extract.key == "liferay/certificates/argocd-cert"
		error_message="An already prefixed TLS secret name must not be prefixed again"
	}
	command=plan
	variables {
		argocd_domain_config={
			hostname="argocd.example.com"
			tls_external_secret_name="liferay/certificates/argocd-cert"
		}
	}
}
variables {
	deployment_name="liferay-test"
	infrastructure_helm_chart_version="0.4.9"
	infrastructure_provider_helm_chart_version="0.3.12"
	liferay_git_repo_url="https://github.com/example/liferay-gitops.git"
	liferay_helm_chart_version="0.4.20"
	region="us-east-1"
}