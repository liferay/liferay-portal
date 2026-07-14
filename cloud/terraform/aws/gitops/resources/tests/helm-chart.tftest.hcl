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
run "should_configure_the_liferay_aws_marketplace_chart" {
	assert {
		condition=length(aws_iam_role_policy.aws_marketplace_metering_policy) == 1 && local.aws_marketplace_enabled == true
		error_message="The liferay-aws-marketplace chart must enable the AWS Marketplace metering policy"
	}
	assert {
		condition=local.liferay_helm_chart_config.region == "us-east-1" && tostring(local.liferay_helm_chart_config.ecr_credentials_sync_required) == "true"
		error_message="The liferay-aws-marketplace chart must use the specified region and must require ECR credential sync"
	}
	assert {
		condition=local.liferay_helm_chart_config.values_scope_prefix == "liferay-aws.liferay-default."
		error_message="The liferay-aws-marketplace chart must include prefixes for both liferay-aws and liferay-default charts"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-aws-marketplace"
	}
}
run "should_configure_the_liferay_default_chart" {
	assert {
		condition=local.liferay_helm_chart_config.values_scope_prefix == "" && tostring(local.liferay_helm_chart_config.ecr_credentials_sync_required) == "false"
		error_message="The liferay-default chart must not have scopes defined, and must not require ECR credential sync"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-default"
	}
}
run "should_not_accept_an_unknown_helm_chart_name" {
	command=plan
	expect_failures=[var.liferay_helm_chart_name]
	variables {
		liferay_helm_chart_name="liferay-invalid-chart-name"
	}
}
run "should_use_the_liferay_aws_chart_by_default" {
	assert {
		condition=length(aws_iam_role_policy.aws_marketplace_metering_policy) == 0
		error_message="The AWS Marketplace metering policy must not exist for the liferay-aws chart"
	}
	assert {
		condition=local.aws_marketplace_enabled == false
		error_message="The AWS Marketplace must be disabled for the liferay-aws chart"
	}
	assert {
		condition=local.liferay_helm_chart_config.region == "us-east-1" && tostring(local.liferay_helm_chart_config.ecr_credentials_sync_required) == "false"
		error_message="The liferay-aws chart must use the deployment region and require no ECR credential sync"
	}
	assert {
		condition=local.liferay_helm_chart_config.values_scope_prefix == "liferay-default."
		error_message="The liferay-aws chart must scope values under liferay-default"
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