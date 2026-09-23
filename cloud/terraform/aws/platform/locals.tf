locals {
	account_id=data.aws_caller_identity.current.account_id
	cluster_secret_store_provider=local.default_secrets_manager_enabled ? {
		aws={
			region=var.region
			service="SecretsManager"
		}
	} : var.cluster_secret_store.provider_hcl
	crossplane_data_policies={
		backup=data.aws_iam_policy_document.crossplane_data_backup.json
		cloudwatchlogs=data.aws_iam_policy_document.crossplane_data_cloudwatchlogs.json
		ec2=data.aws_iam_policy_document.crossplane_data_ec2.json
		kms=data.aws_iam_policy_document.crossplane_data_kms.json
		opensearch=data.aws_iam_policy_document.crossplane_data_opensearch.json
		rds=data.aws_iam_policy_document.crossplane_data_rds.json
		s3=data.aws_iam_policy_document.crossplane_data_s3.json
	}
	crossplane_iam_path="/crossplane/${var.deployment_name}/"
	default_secrets_manager_enabled=var.cluster_secret_store.provider_hcl == null
	deployment_context={
		accountId=local.account_id
		clusterSecurityGroupId=data.aws_eks_cluster.eks.vpc_config[0].cluster_security_group_id
		clusterSubnetIds=jsonencode(data.aws_eks_cluster.eks.vpc_config[0].subnet_ids)
		crossplaneDataRoleArn=aws_iam_role.crossplane_data.arn
		crossplaneIamBoundaryArn=aws_iam_policy.crossplane_iam_boundary.arn
		crossplaneIamPath=local.crossplane_iam_path
		crossplaneIamRoleArn=aws_iam_role.crossplane_iam.arn
		deploymentName=var.deployment_name
		liferayServiceAccountRoleName=data.aws_iam_role.liferay.name
		oidcIssuerUrl=data.aws_eks_cluster.eks.identity[0].oidc[0].issuer
		partition=local.partition
		region=var.region
		vpcId=data.aws_eks_cluster.eks.vpc_config[0].vpc_id
	}
	irsa_service_accounts=merge(
		{
			crossplane_data="crossplane-system:crossplane-data"
			crossplane_iam="crossplane-system:crossplane-iam"
			external_secrets="external-secrets-system:external-secrets"
		},
		local.keda_enabled ? {
			keda="${var.keda_config.namespace}:${var.keda_config.service_account_name}"
		} : {},
		var.observability_config.enabled ? {
			ack_prometheusservice="ack-system:ack-prometheusservice-controller"
		} : {})
	keda_enabled=var.keda_config.enabled && var.observability_config.enabled
	marketplace_posix_id=1000
	oidc_provider=trimprefix(data.aws_eks_cluster.eks.identity[0].oidc[0].issuer, "https://")
	partition=data.aws_partition.current.partition
}