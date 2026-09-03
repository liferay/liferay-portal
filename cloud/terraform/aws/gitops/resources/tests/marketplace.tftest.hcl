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
	target=data.aws_efs_file_system.marketplace
	values={
		id="fs-0123456789abcdef0"
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
override_resource {
	override_during=plan
	target=aws_efs_access_point.marketplace
	values={
		id="fsap-0123456789abcdef0"
	}
}
run "should_own_the_marketplace_directory_as_the_liferay_user" {
	assert {
		condition=aws_efs_access_point.marketplace.posix_user[0].gid == 1000 && aws_efs_access_point.marketplace.posix_user[0].uid == 1000
		error_message="The marketplace access point must enforce the Liferay POSIX identity so the operator writes as a known user"
	}
	assert {
		condition=aws_efs_access_point.marketplace.root_directory[0].creation_info[0].owner_gid == 1000 && aws_efs_access_point.marketplace.root_directory[0].creation_info[0].owner_uid == 1000
		error_message="The marketplace access point must create its root directory owned by the Liferay POSIX identity"
	}
	assert {
		condition=aws_efs_access_point.marketplace.root_directory[0].creation_info[0].permissions == "0755"
		error_message="The marketplace access point root directory must be writable by its owner and readable by the Liferay pods"
	}
	assert {
		condition=aws_efs_access_point.marketplace.root_directory[0].path == "/marketplace"
		error_message="The marketplace access point must be rooted at the marketplace directory"
	}
	command=plan
}
run "should_qualify_the_marketplace_volume_handles_with_the_access_point" {
	assert {
		condition=one([
			for parameter in kubernetes_manifest.infrastructure_provider_application.manifest.spec.sources[0].helm.parameters :
			parameter.value
			if parameter.name == "liferay-dxp-operator.marketplace.csi.volumeHandle"
		]) == "fs-0123456789abcdef0::fsap-0123456789abcdef0"
		error_message="The operator marketplace volume must be mounted through the access point"
	}
	assert {
		condition=one([
			for parameter in kubernetes_manifest.liferay_applicationset.manifest.spec.template.spec.sources[0].helm.parameters :
			parameter.value
			if parameter.name == "liferay-default.marketplace.csi.volumeHandle"
		]) == "fs-0123456789abcdef0::fsap-0123456789abcdef0"
		error_message="The Liferay marketplace volume must be mounted through the same access point as the operator"
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