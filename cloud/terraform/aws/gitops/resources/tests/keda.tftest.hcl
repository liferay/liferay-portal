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
run "should_create_the_amp_read_policy_and_sa_annotation_when_keda_is_enabled" {
	assert {
		condition=aws_iam_role_policy.keda_amp_read[0].name == "liferay-test-eks-keda-amp-read-policy" && length(aws_iam_role_policy.keda_amp_read) == 1
		error_message="The AMP read policy must be created if KEDA is enabled"
	}
	assert {
		condition=jsondecode(aws_iam_role_policy.keda_amp_read[0].policy).Statement[0].Resource == "arn:aws:aps:us-east-1:123456789012:workspace/*"
		error_message="The KEDA AMP read policy must be scoped to Prometheus workspaces in the deployment account and region"
	}
	assert {
		condition=kubernetes_annotations.keda_operator_sa[0].metadata[0].name == "keda-operator" && length(kubernetes_annotations.keda_operator_sa) == 1
		error_message="The keda-operator service account for IRSA must be annotated if KEDA is enabled"
	}
	command=plan
	variables {
		keda_enabled=true
	}
}
run "should_create_the_irsa_role_when_keda_is_enabled" {
	assert {
		condition=aws_iam_role.keda[0].name == "liferay-test-eks-keda-irsa" && length(aws_iam_role.keda) == 1
		error_message="The KEDA IRSA role named after the cluster if KEDA is enabled"
	}
	assert {
		condition=jsondecode(aws_iam_role.keda[0].assume_role_policy).Statement[0].Condition.StringEquals["oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE:sub"] == "system:serviceaccount:keda-system:keda-operator"
		error_message="The KEDA trust policy must scope to the keda-operator service account"
	}
	assert {
		condition=jsondecode(aws_iam_role.keda[0].assume_role_policy).Statement[0].Principal.Federated == "arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
		error_message="The KEDA trust policy must use the constructed OIDC provider ARN"
	}
	command=plan
	variables {
		keda_enabled=true
	}
}
run "should_disable_keda_by_default" {
	assert {
		condition=length(aws_iam_role.keda) == 0 && length(aws_iam_role_policy.keda_amp_read) == 0
		error_message="No KEDA IAM resources should exist when keda_enabled is false"
	}
	assert {
		condition=length(kubernetes_annotations.keda_operator_sa) == 0
		error_message="No KEDA service account annotation should exist when keda_enabled is false"
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