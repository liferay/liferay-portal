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
run "should_configure_crossplane_functions_and_runtime_configs" {
	assert {
		condition=kubernetes_manifest.function_auto_ready.manifest.spec.package == "xpkg.upbound.io/upbound/function-auto-ready:v0.6.0"
		error_message="The auto-ready function must pin its expected package version"
	}
	assert {
		condition=kubernetes_manifest.function_auto_ready_runtime_config.manifest.spec.deploymentTemplate.metadata.annotations["sidecar.opentelemetry.io/inject"] == "false"
		error_message="The Function runtime configs must disable OpenTelemetry sidecar injection"
	}
	assert {
		condition=kubernetes_manifest.function_auto_ready_runtime_config.manifest.spec.deploymentTemplate.spec.template.spec.securityContext.runAsNonRoot == true
		error_message="The Function runtime configs must run as nonroot"
	}
	assert {
		condition=kubernetes_manifest.function_go_templating.manifest.spec.package == "xpkg.upbound.io/crossplane-contrib/function-go-templating:v0.11.3"
		error_message="The go-templating function must pin its expected package version"
	}
	command=plan
}

run "should_name_the_provider_iam_roles_and_policies" {
	assert {
		condition=aws_iam_policy.provider_aws_s3_policy.name == "liferay-test-eks-provider-aws-s3"
		error_message="The S3 Crossplane provider policy must be named after the cluster"
	}
	assert {
		condition=aws_iam_role.provider_aws_kms_role.name == "liferay-test-eks-provider-aws-kms-role" && aws_iam_role.provider_aws_rds_role.name == "liferay-test-eks-provider-aws-rds-role"
		error_message="Every Crossplane provider role must embed the cluster name and provider key"
	}
	assert {
		condition=aws_iam_role.provider_aws_s3_role.name == "liferay-test-eks-provider-aws-s3-role"
		error_message="The S3 Crossplane provider role must be named after the cluster"
	}
	assert {
		condition=aws_iam_role_policy_attachment.provider_aws_s3_attachment.role == "liferay-test-eks-provider-aws-s3-role"
		error_message="The S3 provider policy must attach to the S3 provider role"
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