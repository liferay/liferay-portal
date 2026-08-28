mock_provider "aws" {}
mock_provider "helm" {}
mock_provider "kubernetes" {}
mock_provider "random" {}
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
	}
}
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
		condition=helm_release.external_secrets.create_namespace == true && helm_release.external_secrets.wait == true
		error_message="The External Secrets Helm release must create its namespace and wait"
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
variables {
	ack_prometheusservice_helm_chart_version="1.4.1"
	argo_workflows_helm_chart_version="2.0.3"
	argocd_helm_chart_version="9.5.16"
	crossplane_helm_chart_version="2.1.3"
	deployment_name="liferay-test"
	external_secrets_helm_chart_version="1.0.0"
	keda_helm_chart_version="2.19.0"
	region="us-east-1"
}