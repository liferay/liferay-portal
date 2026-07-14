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
run "should_compute_cluster_identity_locals" {
	assert {
		condition=local.account_id == "123456789012"
		error_message="The local.account_id must come from the caller identity"
	}
	assert {
		condition=local.cluster_name == "liferay-test-eks"
		error_message="The local.cluster_name must include eks as a suffix"
	}
	assert {
		condition=local.liferay_service_account_role_name == "liferay-test-irsa"
		error_message="The local.liferay_service_account_role_name must be derived from deployment_name"
	}
	assert {
		condition=local.oidc_provider == "oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
		error_message="local.oidc_provider must strip the https:// scheme from the EKS OIDC issuer"
	}
	command=plan
}
run "should_not_accept_deployment_name_shorter_than_3_characters" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="ab"
	}
}
run "should_not_accept_uppercase_deployment_name" {
	command=plan
	expect_failures=[var.deployment_name]
	variables {
		deployment_name="Liferay-Test"
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