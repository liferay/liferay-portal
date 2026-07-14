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
run "should_include_required_prefixes_for_the_marketplace_chart_to_gateway_name" {
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters[0].name == "liferay-aws.liferay-default.network.gatewayName"
		error_message="The liferay-aws-marketplace chart must include prefixes for both liferay-aws and liferay-default charts to gatewayName Helm parameter"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-aws-marketplace"
	}
}
run "should_name_the_appprojects" {
	assert {
		condition=kubernetes_manifest.infrastructure_applicationset.manifest.spec.template.spec.project == "liferay-infrastructure"
		error_message="The infrastructure ApplicationSet template must target the liferay-infrastructure project"
	}
	assert {
		condition=kubernetes_manifest.infrastructure_appproject.manifest.metadata.name == "liferay-infrastructure"
		error_message="The infrastructure AppProject must be named liferay-infrastructure"
	}
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.project == "liferay-application"
		error_message="The Liferay ApplicationSet template must target the liferay-application project"
	}
	assert {
		condition=kubernetes_manifest.liferay_appproject.manifest.metadata.name == "liferay-application"
		error_message="The Liferay AppProject must be named liferay-application"
	}
	command=plan
}
run "should_pass_cluster_identity_to_the_provider_application" {
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "aws.clusterName" && p.value == "liferay-test-eks"
		]) == 1
		error_message="The infrastructure provider Application must pass the cluster name as a Helm parameter"
	}
	assert {
		condition=length([
			for p in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters : p
			if p.name == "deploymentName" && p.value == "liferay-test"
		]) == 1
		error_message="The infrastructure provider Application must pass the deployment name as a Helm parameter"
	}
	command=plan
}
run "should_scope_liferay_applicationset_helm_values_by_prefix" {
	assert {
		condition=kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters[0].name == "liferay-default.network.gatewayName"
		error_message="The liferay-aws chart must include prefix for liferay-default chart at gatewayName Helm parameter"
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