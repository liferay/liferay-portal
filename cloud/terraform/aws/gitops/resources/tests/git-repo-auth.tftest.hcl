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
run "should_create_a_single_credentials_secret_for_a_shared_repo" {
	assert {
		condition=contains(keys(kubernetes_manifest.git_repo_credentials_external_secret), "liferay")
		error_message="The single credentials secret must have liferay as a key"
	}
	assert {
		condition=length(kubernetes_manifest.git_repo_credentials_external_secret) == 1
		error_message="A shared infrastructure/Liferay repo URL must link to a single git credentials ExternalSecret"
	}
	command=plan
}
run "should_create_two_credentials_secrets_for_separate_repos" {
	assert {
		condition=contains(keys(kubernetes_manifest.git_repo_credentials_external_secret), "infrastructure") && contains(keys(kubernetes_manifest.git_repo_credentials_external_secret), "liferay")
		error_message="The credential secrets for infrastructure and liferay repos must be created"
	}
	assert {
		condition=length(kubernetes_manifest.git_repo_credentials_external_secret) == 2
		error_message="A distinct infrastructure repo URL must link infrastructure and Liferay credentials secrets"
	}
	command=plan
	variables {
		infrastructure_git_repo_config={
			auth={}
			source_paths={}
			target={}
			url="https://github.com/example/liferay-infrastructure.git"
		}
	}
}
run "should_map_the_private_key_for_ssh_auth" {
	assert {
		condition=kubernetes_manifest.git_repo_credentials_external_secret["liferay"].manifest.spec.data[0].secretKey == "ssh_private_key" && length([
			for d in kubernetes_manifest.git_repo_credentials_external_secret["liferay"].manifest.spec.data : d.secretKey
		]) == 1
		error_message="The SSH auth must map a single ssh_private_key secret key"
	}
	command=plan
	variables {
		liferay_git_repo_config={
			auth={
				method="ssh"
			}
			source_paths={}
			target={}
		}
	}
}
run "should_map_username_and_password_for_https_auth" {
	assert {
		condition=contains([
			for d in kubernetes_manifest.git_repo_credentials_external_secret["liferay"].manifest.spec.data : d.secretKey
		], "password") && contains([
			for d in kubernetes_manifest.git_repo_credentials_external_secret["liferay"].manifest.spec.data : d.secretKey
		], "username")
		error_message="The HTTPS auth must map both username and password secret keys"
	}
	assert {
		condition=length([
			for d in kubernetes_manifest.git_repo_credentials_external_secret["liferay"].manifest.spec.data : d.secretKey
		]) == 2
		error_message="The HTTPS auth must map exactly two secret keys"
	}
	command=plan
}
run "should_not_accept_an_invalid_git_auth_method" {
	command=plan
	expect_failures=[var.liferay_git_repo_config]
	variables {
		liferay_git_repo_config={
			auth={
				method="token"
			}
			source_paths={}
			target={}
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