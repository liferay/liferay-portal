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
run "should_create_ecr_sync_resources_for_the_marketplace_chart" {
	assert {
		condition=aws_iam_role.ecr_role[0].name == "liferay-test-eks-gitops-ecr-credentials-sync" && length(aws_iam_role.ecr_role) == 1
		error_message="The liferay-aws-marketplace chart must create an IAM role for ECR sync"
	}
	assert {
		condition=contains(keys(kubernetes_service_account.ecr_sa[0].metadata[0].annotations), "eks.amazonaws.com/role-arn")
		error_message="The ECR sync IAM role must carry the role-arn annotation for the IAM role"
	}
	assert {
		condition=kubernetes_cron_job_v1.ecr_credentials_sync[0].spec[0].schedule == "0 */8 * * *" && length(kubernetes_cron_job_v1.ecr_credentials_sync) == 1
		error_message="The liferay-aws-marketplace chart must schedule the ECR credentials sync CronJob every 8 hours"
	}
	assert {
		condition=length(kubernetes_job_v1.ecr_credentials_sync_initial) == 1
		error_message="The liferay-aws-marketplace chart must create the one time initial ECR credentials sync Job"
	}
	assert {
		condition=length(kubernetes_role.ecr_secret_manager) == 1 && length(kubernetes_role_binding.ecr_secret_manager_binding) == 1 && length(kubernetes_service_account.ecr_sa) == 1
		error_message="The liferay-aws-marketplace chart must create the ECR sync RBAC role, binding, and service account"
	}
	command=plan
	variables {
		liferay_helm_chart_name="liferay-aws-marketplace"
	}
}
run "should_not_create_ecr_sync_resources_for_a_non_marketplace_chart" {
	assert {
		condition=length(aws_iam_role.ecr_role) == 0 && length(aws_iam_role_policy_attachment.ecr_policy) == 0
		error_message="The IAM resources for ECR sync should not exist if the chart does not require ECR credential sync"
	}
	assert {
		condition=length(kubernetes_cron_job_v1.ecr_credentials_sync) == 0 && length(kubernetes_job_v1.ecr_credentials_sync_initial) == 0
		error_message="The ECR sync jobs should not exist if the chart does not require ECR credential sync"
	}
	assert {
		condition=length(kubernetes_role.ecr_secret_manager) == 0 && length(kubernetes_role_binding.ecr_secret_manager_binding) == 0 && length(kubernetes_service_account.ecr_sa) == 0
		error_message="The ECR sync RBAC resources should not exist if the chart does not require ECR credential sync"
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