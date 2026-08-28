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
run "should_honor_a_custom_ack_namespace" {
	assert {
		condition=helm_release.ack_prometheusservice[0].namespace == "ack"
		error_message="A custom ack_namespace must flow to the Helm release"
	}
	command=plan
	variables {
		ack_namespace="ack"
		observability_config={
			enabled=true
		}
	}
}
run "should_install_ack_prometheusservice_when_observability_is_enabled" {
	assert {
		condition=length(helm_release.ack_prometheusservice) == 1
		error_message="The ACK Prometheus Service controller must be installed when observability is enabled"
	}
	assert {
		condition=helm_release.ack_prometheusservice[0].version == var.ack_prometheusservice_helm_chart_version
		error_message="The ACK Prometheus Service Helm release must use the configured chart version"
	}
	assert {
		condition=helm_release.ack_prometheusservice[0].create_namespace == true && helm_release.ack_prometheusservice[0].namespace == "ack-system"
		error_message="The ACK Prometheus Service Helm release must default to the ack-system namespace and create it"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_not_install_ack_prometheusservice_by_default" {
	assert {
		condition=length(helm_release.ack_prometheusservice) == 0
		error_message="The ACK Prometheus Service controller must not be installed when observability is disabled"
	}
	command=plan
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