mock_provider "aws" {
	mock_data "aws_iam_policy_document" {
		defaults={
			json="{\"Statement\": [], \"Version\": \"2012-10-17\"}"
		}
	}
	mock_resource "aws_efs_access_point" {
		defaults={
			id="fsap-0123456789abcdef0"
		}
	}
	mock_resource "aws_iam_role" {
		defaults={
			arn="arn:aws:iam::123456789012:role/mock"
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
	target=data.aws_eks_cluster.eks
	values={
		identity=[
			{
				oidc=[
					{
						issuer="https://oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
					},
				]
			},
		]
		vpc_config=[
			{
				cluster_security_group_id="sg-0123456789abcdef0"
				subnet_ids=["subnet-aaa", "subnet-bbb"]
				vpc_id="vpc-0123456789abcdef0"
			},
		]
	}
}
override_data {
	target=data.aws_iam_openid_connect_provider.eks
	values={
		arn="arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
	}
}
override_data {
	target=data.aws_iam_role.liferay
	values={
		name="liferay-test-irsa"
	}
}
override_data {
	target=data.aws_partition.current
	values={
		partition="aws"
	}
}
override_data {
	target=data.aws_subnet.cluster["subnet-aaa"]
	values={
		cidr_block="10.0.2.0/24"
	}
}
override_data {
	target=data.aws_subnet.cluster["subnet-bbb"]
	values={
		cidr_block="10.0.1.0/24"
	}
}
override_module {
	target=module.argocd
}
run "should_assemble_the_deployment_context" {
	assert {
		condition=join(",", keys(local.deployment_context)) == "accountId,clusterSecurityGroupId,clusterSubnetIds,crossplaneDataRoleArn,crossplaneIamBoundaryArn,crossplaneIamPath,crossplaneIamRoleArn,deploymentName,liferayServiceAccountRoleName,oidcIssuerUrl,partition,region,vpcId"
		error_message="The deployment context must carry exactly the keys the infrastructure provider consumes"
	}
	assert {
		condition=local.deployment_context.accountId == "123456789012"
		error_message="The deployment context must carry the account ID"
	}
	assert {
		condition=local.deployment_context.clusterSecurityGroupId == "sg-0123456789abcdef0"
		error_message="The deployment context must carry the cluster security group"
	}
	assert {
		condition=local.deployment_context.clusterSubnetIds == "[\"subnet-aaa\",\"subnet-bbb\"]"
		error_message="The deployment context must carry the cluster subnets as a sorted JSON list"
	}
	assert {
		condition=local.deployment_context.deploymentName == "liferay-test"
		error_message="The deployment context must carry the deployment name"
	}
	assert {
		condition=local.deployment_context.crossplaneIamPath == "/crossplane/liferay-test/"
		error_message="The deployment context must carry the IAM path the composition creates its principals under"
	}
	assert {
		condition=local.deployment_context.crossplaneIamBoundaryArn == aws_iam_policy.crossplane_iam_boundary.arn
		error_message="The deployment context must carry the permissions boundary the composition attaches to its principals"
	}
	assert {
		condition=local.deployment_context.liferayServiceAccountRoleName == "liferay-test-irsa"
		error_message="The deployment context must carry the Liferay service account role name"
	}
	assert {
		condition=local.deployment_context.oidcIssuerUrl == "https://oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"
		error_message="The deployment context must carry the full OIDC issuer URL"
	}
	assert {
		condition=local.deployment_context.partition == "aws"
		error_message="The deployment context must carry the partition"
	}
	assert {
		condition=local.deployment_context.region == "us-east-1"
		error_message="The deployment context must carry the region"
	}
	assert {
		condition=local.deployment_context.vpcId == "vpc-0123456789abcdef0"
		error_message="The deployment context must carry the VPC ID"
	}
	command=plan
	override_resource {
		override_during=plan
		target=aws_iam_policy.crossplane_iam_boundary
		values={
			arn="arn:aws:iam::123456789012:policy/liferay-test-crossplane-iam-boundary"
		}
	}
}
run "should_build_the_secrets_manager_secret_store_provider" {
	assert {
		condition=join(",", keys(local.cluster_secret_store_provider)) == "aws"
		error_message="The default branch must produce an AWS provider"
	}
	assert {
		condition=local.cluster_secret_store_provider.aws.service == "SecretsManager"
		error_message="The AWS provider must use Secrets Manager"
	}
	assert {
		condition=local.cluster_secret_store_provider.aws.region == "us-east-1"
		error_message="The AWS provider must use the deployment region"
	}
	assert {
		condition=join(",", keys(local.cluster_secret_store_provider.aws)) == "region,service"
		error_message="The AWS provider must carry only the region and service, so it authenticates through the External Secrets controller's IRSA role"
	}
	assert {
		condition=length(aws_iam_role_policy.external_secrets) == 1
		error_message="The External Secrets role must be granted Secrets Manager access"
	}
	command=plan
}
run "should_create_the_ack_prometheusservice_role_when_observability_is_enabled" {
	assert {
		condition=aws_iam_role.ack_prometheusservice[0].name == "liferay-test-ack-prometheusservice"
		error_message="The ACK Prometheus service role name must be derived from deployment_name"
	}
	assert {
		condition=local.irsa_service_accounts.ack_prometheusservice == "ack-system:ack-prometheusservice-controller"
		error_message="The ACK Prometheus service role must trust the ACK controller service account"
	}
	assert {
		condition=length(aws_iam_role_policy.ack_prometheusservice) == 1
		error_message="The ACK Prometheus service role must be granted AMP rule group access"
	}
	command=plan
	variables {
		observability_config={
			enabled=true
		}
	}
}
run "should_create_the_keda_role_when_enabled" {
	assert {
		condition=aws_iam_role.keda[0].name == "liferay-test-keda"
		error_message="The KEDA role name must be derived from deployment_name"
	}
	assert {
		condition=local.irsa_service_accounts.keda == "keda-system:keda-operator"
		error_message="The KEDA role must trust the keda-operator service account in the KEDA namespace"
	}
	assert {
		condition=length(aws_iam_role_policy.keda) == 1
		error_message="The KEDA role must be granted AMP query access"
	}
	assert {
		condition=output.keda_service_account_namespace == "keda-system"
		error_message="The KEDA service account namespace must be published"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
		}
		observability_config={
			enabled=true
		}
	}
}
run "should_derive_the_kubernetes_endpoint_cidrs_from_the_cluster_subnets" {
	assert {
		condition=join(",", output.kubernetes_endpoint_cidrs) == "10.0.1.0/24,10.0.2.0/24"
		error_message="The Kubernetes endpoint CIDRs must be the sorted CIDR blocks of the cluster subnets"
	}
	command=plan
}
run "should_fence_crossplane_iam_with_a_permissions_boundary" {
	assert {
		condition=aws_iam_policy.crossplane_iam_boundary.name == "liferay-test-crossplane-iam-boundary"
		error_message="The boundary name must be derived from deployment_name"
	}
	assert {
		condition=!startswith(coalesce(aws_iam_policy.crossplane_iam_boundary.path, "/"), local.crossplane_iam_path)
		error_message="The boundary must live outside the path crossplane-iam manages, so crossplane-iam cannot edit it"
	}
	assert {
		condition=alltrue([for statement in data.aws_iam_policy_document.crossplane_iam.statement : alltrue([for resource in statement.resources : resource == "*" || strcontains(resource, "/crossplane/liferay-test/")])])
		error_message="Every crossplane-iam write statement must be confined to the managed IAM path"
	}
	assert {
		condition=alltrue([for statement in data.aws_iam_policy_document.crossplane_iam.statement : statement.resources != tolist(["*"]) || alltrue([for action in statement.actions : startswith(action, "iam:Get") || startswith(action, "iam:List")])])
		error_message="crossplane-iam may only read IAM outside the managed path"
	}
	assert {
		condition=contains(data.aws_iam_policy_document.crossplane_iam.statement[1].actions, "iam:CreateRole") && one(data.aws_iam_policy_document.crossplane_iam.statement[1].condition).variable == "iam:PermissionsBoundary" && one(one(data.aws_iam_policy_document.crossplane_iam.statement[1].condition).values) == aws_iam_policy.crossplane_iam_boundary.arn
		error_message="Creating principals and changing their policies must require the permissions boundary"
	}
	assert {
		condition=toset(data.aws_iam_policy_document.crossplane_iam.statement[1].resources) == toset(["arn:aws:iam::123456789012:role/crossplane/liferay-test/*", "arn:aws:iam::123456789012:user/crossplane/liferay-test/*"])
		error_message="The boundary conditioned actions must target roles and users under the managed path"
	}
	assert {
		condition=contains(data.aws_iam_policy_document.crossplane_iam.statement[2].actions, "iam:UpdateAssumeRolePolicy") && toset(data.aws_iam_policy_document.crossplane_iam.statement[2].resources) == toset(["arn:aws:iam::123456789012:role/crossplane/liferay-test/*", "arn:aws:iam::123456789012:user/crossplane/liferay-test/*"])
		error_message="Trust and lifecycle changes must be confined to principals under the managed path"
	}
	assert {
		condition=toset(data.aws_iam_policy_document.crossplane_iam.statement[3].resources) == toset(["arn:aws:iam::123456789012:policy/crossplane/liferay-test/*"])
		error_message="Managed policy changes must be confined to the managed path"
	}
	assert {
		condition=alltrue([for statement in data.aws_iam_policy_document.crossplane_iam.statement : !anytrue([for action in statement.actions : strcontains(action, "PermissionsBoundary")])])
		error_message="crossplane-iam must not be able to remove or replace a permissions boundary"
	}
	command=plan
	override_resource {
		override_during=plan
		target=aws_iam_policy.crossplane_iam_boundary
		values={
			arn="arn:aws:iam::123456789012:policy/liferay-test-crossplane-iam-boundary"
		}
	}
}
run "should_grant_the_crossplane_roles_their_provider_permissions" {
	assert {
		condition=join(",", keys(aws_iam_role_policy.crossplane_data)) == "backup,cloudwatchlogs,ec2,kms,opensearch,rds,s3"
		error_message="The crossplane-data role must carry one policy per data provider"
	}
	assert {
		condition=aws_iam_role_policy.crossplane_iam.name == "iam"
		error_message="The crossplane-iam role must carry the IAM provider policy"
	}
	command=plan
}
run "should_honor_a_custom_keda_service_account" {
	assert {
		condition=local.irsa_service_accounts.keda == "keda-custom:keda-custom-operator"
		error_message="The KEDA role must trust the configured service account"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
			namespace="keda-custom"
			service_account_name="keda-custom-operator"
		}
		observability_config={
			enabled=true
		}
	}
}
run "should_inject_an_external_secret_store_provider" {
	assert {
		condition=join(",", keys(local.cluster_secret_store_provider)) == "vault"
		error_message="The provider_hcl branch must pass the custom provider through verbatim"
	}
	assert {
		condition=length(aws_iam_role_policy.external_secrets) == 0
		error_message="The External Secrets role must not be granted Secrets Manager access with a custom provider"
	}
	command=plan
	variables {
		cluster_secret_store={
			provider_hcl={
				vault={
					server="https://vault.example.com:8200"
				}
			}
		}
	}
}
run "should_limit_what_bounded_principals_can_do" {
	assert {
		condition=join(",", one([for statement in data.aws_iam_policy_document.crossplane_data_backup.statement : statement if contains(statement.actions, "iam:PassRole")]).resources) == "arn:aws:iam::123456789012:role/crossplane/liferay-test/*"
		error_message="crossplane-data may only pass roles under the managed path"
	}
	assert {
		condition=join(",", sort(one(one([for statement in data.aws_iam_policy_document.crossplane_data_backup.statement : statement if contains(statement.actions, "iam:PassRole")]).condition).values)) == "backup.amazonaws.com"
		error_message="crossplane-data may only pass roles to AWS Backup"
	}
	assert {
		condition=!anytrue([for statement in data.aws_iam_policy_document.crossplane_iam_boundary.statement : anytrue([for action in statement.actions : startswith(action, "iam:") && action != "iam:PassRole"])])
		error_message="The boundary must grant no IAM action beyond passing roles to AWS Backup"
	}
	assert {
		condition=!anytrue([for statement in data.aws_iam_policy_document.crossplane_iam_boundary.statement : anytrue([for action in statement.actions : startswith(action, "sts:") || action == "*"])])
		error_message="The boundary must grant no STS action and no wildcard"
	}
	command=plan
}
run "should_omit_the_ack_prometheusservice_role_by_default" {
	assert {
		condition=length(aws_iam_role.ack_prometheusservice) == 0
		error_message="The ACK Prometheus service role must not be created unless observability is enabled"
	}
	assert {
		condition=output.ack_prometheusservice_role_arn == ""
		error_message="The ACK Prometheus service role ARN must be empty unless observability is enabled"
	}
	command=plan
}
run "should_omit_the_keda_role_by_default" {
	assert {
		condition=length(aws_iam_role.keda) == 0
		error_message="The KEDA role must not be created by default"
	}
	assert {
		condition=output.keda_role_arn == ""
		error_message="The KEDA role ARN must be empty by default"
	}
	command=plan
}
run "should_omit_the_keda_role_when_observability_is_disabled" {
	assert {
		condition=length(aws_iam_role.keda) == 0
		error_message="The KEDA role must not be created when observability is disabled"
	}
	command=plan
	variables {
		keda_config={
			enabled=true
		}
	}
}
run "should_publish_the_marketplace_volume_handle" {
	assert {
		condition=output.marketplace_volume_handle == "fs-0123456789abcdef0::fsap-0123456789abcdef0"
		error_message="The marketplace volume handle must join the file system and access point IDs"
	}
	assert {
		condition=aws_efs_access_point.marketplace.root_directory[0].path == "/marketplace"
		error_message="The marketplace access point must be rooted at /marketplace"
	}
	command=apply
}
run "should_wire_the_platform_roles" {
	assert {
		condition=aws_iam_role.crossplane_data.name == "liferay-test-crossplane-data"
		error_message="The crossplane-data role name must be derived from deployment_name"
	}
	assert {
		condition=aws_iam_role.crossplane_iam.name == "liferay-test-crossplane-iam"
		error_message="The crossplane-iam role name must be derived from deployment_name"
	}
	assert {
		condition=aws_iam_role.external_secrets.name == "liferay-test-external-secrets"
		error_message="The External Secrets role name must be derived from deployment_name"
	}
	assert {
		condition=local.irsa_service_accounts.crossplane_data == "crossplane-system:crossplane-data"
		error_message="The crossplane-data role must trust the crossplane-data service account"
	}
	assert {
		condition=local.irsa_service_accounts.crossplane_iam == "crossplane-system:crossplane-iam"
		error_message="The crossplane-iam role must trust the crossplane-iam service account"
	}
	assert {
		condition=local.irsa_service_accounts.external_secrets == "external-secrets-system:external-secrets"
		error_message="The External Secrets role must trust the External Secrets controller service account"
	}
	command=plan
}
variables {
	argocd_helm_chart_version="10.1.3"
	deployment_name="liferay-test"
	region="us-east-1"
}