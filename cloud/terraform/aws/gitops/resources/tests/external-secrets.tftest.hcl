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
run "should_create_the_secret_store_with_the_default_provider" {
	assert {
		condition=aws_iam_role.secret_store_role[0].name == "liferay-test-eks-secret-store" && length(aws_iam_role.secret_store_role) == 1
		error_message="The default provider must create the secret store IRSA role named after the cluster"
	}
	assert {
		condition=kubernetes_cluster_role.eso_secret_writer.metadata[0].name == "eso-cluster-secret-writer"
		error_message="The External Secrets writer ClusterRole must be named eso-cluster-secret-writer"
	}
	assert {
		condition=kubernetes_manifest.secret_store.manifest.metadata.name == "liferay-test-secret-store"
		error_message="The ClusterSecretStore name must be derived from deployment_name"
	}
	assert {
		condition=kubernetes_manifest.secret_store.manifest.spec.provider.aws.service == "SecretsManager"
		error_message="The default ClusterSecretStore must use the AWS Secrets Manager provider"
	}
	assert {
		condition=length(aws_iam_policy.secret_store_policy) == 1 && length(kubernetes_service_account.secret_store_sa) == 1
		error_message="The default provider must create the secret store policy and service account"
	}
	command=plan
}
run "should_create_the_sso_external_secret_when_enabled" {
	assert {
		condition=length(kubernetes_manifest.argocd_sso_saml_external_secret) == 1
		error_message="The customer-idp-saml ExternalSecret should be created if SSO is enabled"
	}
	assert {
		condition=length(kubernetes_manifest.argocd_sso_saml_external_secret[0].manifest.spec.data) == 4
		error_message="The SAML SSO ExternalSecret must map caData, entityIssuer, redirectURI, and ssoURL"
	}
	command=plan
	variables {
		argocd_sso_config={
			enable_saml_sso=true
		}
	}
}
run "should_disable_default_irsa_for_a_custom_secret_store_provider" {
	assert {
		condition=kubernetes_manifest.secret_store.manifest.spec.provider.aws.service == "SecretsManager"
		error_message="The ClusterSecretStore must still be created from the custom provider HCL"
	}
	assert {
		condition=length(aws_iam_policy.secret_store_policy) == 0 && length(aws_iam_role.secret_store_role) == 0 && length(kubernetes_service_account.secret_store_sa) == 0
		error_message="Supplying a custom secret store provider must disable the default resources for IAM roles"
	}
	command=plan
	variables {
		external_secret_store_provider_hcl={
			aws={
				auth={
					jwt={
						serviceAccountRef={
							name="secret-store-sa"
							namespace="external-secrets-system"
						}
					}
				}
				region="us-east-1"
				service="SecretsManager"
			}
		}
	}
}
run "should_not_create_the_sso_external_secret_by_default" {
	assert {
		condition=length(kubernetes_manifest.argocd_sso_saml_external_secret) == 0
		error_message="No SAML SSO ExternalSecret should exist when SAML SSO is disabled"
	}
	command=plan
}
variables {
	deployment_name="liferay-test"
	infrastructure_helm_chart_version="0.4.9"
	infrastructure_provider_helm_chart_version="0.3.12"
	liferay_git_repo_url="https://github.com/example/liferay-gitops.git"
	liferay_helm_chart_version="0.4.20"
	region="us-east-1"
}