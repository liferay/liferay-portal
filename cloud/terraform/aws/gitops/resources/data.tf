data "aws_caller_identity" "current" {
}
data "aws_efs_file_system" "marketplace" {
	creation_token="${var.deployment_name}-marketplace"
}
data "aws_eks_cluster" "cluster" {
	name=local.cluster_name
	region=var.region
}
data "aws_iam_policy_document" "provider_aws_backup_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-backup*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_backup_policy_document" {
	statement {
		actions=["backup:*"]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["backup-storage:*"]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"iam:GetRole",
			"iam:PassRole"
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["kms:CreateGrant"]
		effect="Allow"
		condition {
			test="ForAnyValue:StringEquals"
			values=["aws:backup:backup-vault"]
			variable="kms:EncryptionContextKeys"
		}
		condition {
			test="Bool"
			values=["true"]
			variable="kms:GrantIsForAWSResource"
		}
		condition {
			test="StringLike"
			values=["backup.*.amazonaws.com"]
			variable="kms:ViaService"
		}
		resources=["*"]
	}
	statement {
		actions=["kms:DescribeKey"]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "provider_aws_cloudwatchlogs_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-cloudwatchlogs*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_cloudwatchlogs_policy_document" {
	statement {
		actions=[
			"logs:CreateLogGroup",
			"logs:DeleteLogGroup",
			"logs:DeleteResourcePolicy",
			"logs:DeleteRetentionPolicy",
			"logs:DescribeLogGroups",
			"logs:DescribeResourcePolicies",
			"logs:ListTagsForResource",
			"logs:PutResourcePolicy",
			"logs:PutRetentionPolicy",
			"logs:TagResource",
			"logs:UntagResource",
		]
		effect="Allow"
		resources=["arn:aws:logs:${var.region}:${local.account_id}:log-group:*"]
	}
}
data "aws_iam_policy_document" "provider_aws_ec2_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-ec2*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_ec2_policy_document" {
	statement {
		actions=[
			"ec2:AuthorizeSecurityGroupIngress",
			"ec2:CreateNetworkInterface",
			"ec2:CreateSecurityGroup",
			"ec2:CreateTags",
			"ec2:DeleteNetworkInterface",
			"ec2:DeleteSecurityGroup",
			"ec2:DescribeAvailabilityZones",
			"ec2:DescribeNetworkInterfaces",
			"ec2:DescribeSecurityGroupRules",
			"ec2:DescribeSecurityGroups",
			"ec2:DescribeSubnets",
			"ec2:DescribeVpcs",
			"ec2:ModifyNetworkInterfaceAttribute",
			"ec2:ModifySecurityGroupRules",
			"ec2:RevokeSecurityGroupIngress",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "provider_aws_iam_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-iam*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_iam_policy_document" {
	statement {
		actions=[
			"iam:AttachRolePolicy",
			"iam:AttachUserPolicy",
			"iam:CreateAccessKey",
			"iam:CreatePolicy",
			"iam:CreatePolicyVersion",
			"iam:CreateRole",
			"iam:CreateUser",
			"iam:DeleteAccessKey",
			"iam:DeletePolicy",
			"iam:DeletePolicyVersion",
			"iam:DeleteRole",
			"iam:DeleteUser",
			"iam:DeleteUserPolicy",
			"iam:DetachRolePolicy",
			"iam:DetachUserPolicy",
			"iam:GetAccessKeyLastUsed",
			"iam:GetPolicy",
			"iam:GetPolicyVersion",
			"iam:GetRole",
			"iam:GetUser",
			"iam:GetUserPolicy",
			"iam:ListAccessKeys",
			"iam:ListAttachedRolePolicies",
			"iam:ListAttachedUserPolicies",
			"iam:ListGroupsForUser",
			"iam:ListInstanceProfilesForRole",
			"iam:ListPolicyVersions",
			"iam:ListRolePolicies",
			"iam:ListUserPolicies",
			"iam:PutUserPolicy",
			"iam:TagPolicy",
			"iam:TagRole",
			"iam:TagUser",
			"iam:UntagUser",
			"iam:UpdateAccessKey",
			"iam:UpdateAssumeRolePolicy",
			"iam:UpdateUser",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "provider_aws_kms_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-kms*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_kms_policy_document" {
	statement {
		actions=[
			"kms:CancelKeyDeletion",
			"kms:CreateAlias",
			"kms:CreateKey",
			"kms:DeleteAlias",
			"kms:DescribeKey",
			"kms:DisableKey",
			"kms:DisableKeyRotation",
			"kms:EnableKey",
			"kms:EnableKeyRotation",
			"kms:GetKeyPolicy",
			"kms:GetKeyRotationStatus",
			"kms:ListAliases",
			"kms:ListKeys",
			"kms:ListResourceTags",
			"kms:PutKeyPolicy",
			"kms:ScheduleKeyDeletion",
			"kms:TagResource",
			"kms:UntagResource",
			"kms:UpdateAlias",
			"kms:UpdateKeyDescription",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "provider_aws_opensearch_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-opensearch*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_opensearch_policy_document" {
	statement {
		actions=[
			"es:AddTags",
			"es:CreateDomain",
			"es:DeleteDomain",
			"es:DescribeDomain",
			"es:DescribeDomainConfig",
			"es:DescribeDomainHealth",
			"es:DescribeDomainNodes",
			"es:ESHttpGet",
			"es:ESHttpPut",
			"es:ListDomainNames",
			"es:ListTags",
			"es:RemoveTags",
			"es:UpdateDomainConfig",
			"kms:CreateGrant",
			"kms:DescribeKey",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "provider_aws_rds_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-rds*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_rds_policy_document" {
	statement {
		actions=[
			"rds:AddTagsToResource",
			"rds:CreateDBInstance",
			"rds:CreateDBSubnetGroup",
			"rds:DeleteDBInstance",
			"rds:DeleteDBSubnetGroup",
			"rds:DescribeDBInstances",
			"rds:DescribeDBParameters",
			"rds:DescribeDBSubnetGroups",
			"rds:DescribeEngineDefaultParameters",
			"rds:ListTagsForResource",
			"rds:ModifyDBInstance",
			"rds:ModifyDBSubnetGroup",
			"rds:RestoreDBInstanceFromDBSnapshot",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"ec2:AuthorizeSecurityGroupIngress",
			"ec2:CreateSecurityGroup",
			"ec2:CreateTags",
			"ec2:DescribeSecurityGroups",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "provider_aws_s3_assume_role_policy_document" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringLike"
			values=["system:serviceaccount:${var.crossplane_namespace}:provider-aws-s3*"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=["arn:aws:iam::${local.account_id}:oidc-provider/${local.oidc_provider}"]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "provider_aws_s3_policy_document" {
	statement {
		actions=[
			"s3:AbortMultipartUpload",
			"s3:CreateBucket",
			"s3:DeleteBucket",
			"s3:GetAccelerateConfiguration",
			"s3:GetBucketAcl",
			"s3:GetBucketCORS",
			"s3:GetBucketLocation",
			"s3:GetBucketLogging",
			"s3:GetBucketObjectLockConfiguration",
			"s3:GetBucketOwnershipControls",
			"s3:GetBucketPolicy",
			"s3:GetBucketPublicAccessBlock",
			"s3:GetBucketRequestPayment",
			"s3:GetBucketTagging",
			"s3:GetBucketVersioning",
			"s3:GetBucketWebsite",
			"s3:GetEncryptionConfiguration",
			"s3:GetLifecycleConfiguration",
			"s3:GetReplicationConfiguration",
			"s3:ListBucket",
			"s3:ListBucketMultipartUploads",
			"s3:PutBucketAcl",
			"s3:PutBucketCORS",
			"s3:PutBucketOwnershipControls",
			"s3:PutBucketPolicy",
			"s3:PutBucketPublicAccessBlock",
			"s3:PutBucketTagging",
			"s3:PutBucketVersioning",
			"s3:PutBucketWebsite",
			"s3:PutEncryptionConfiguration",
			"s3:PutLifecycleConfiguration",
		]
		effect="Allow"
		resources=["arn:aws:s3:::*"]
	}
	statement {
		actions=[
			"s3:DeleteObject",
			"s3:GetObject",
			"s3:PutObject",
		]
		effect="Allow"
		resources=["arn:aws:s3:::*/*"]
	}
}
data "aws_prometheus_workspace" "amp" {
	count=var.observability_config.enabled && length(try(data.aws_prometheus_workspaces.amp[0].workspace_ids, [])) > 0 ? 1 : 0
	workspace_id=data.aws_prometheus_workspaces.amp[0].workspace_ids[0]
}
data "aws_prometheus_workspaces" "amp" {
	alias_prefix="${var.deployment_name}-amp-workspace"
	count=var.observability_config.enabled ? 1 : 0
}
data "aws_subnet" "private" {
	for_each=toset(data.aws_subnets.private.ids)
	id=each.value
}
data "aws_subnets" "private" {
	filter {
		name="tag:DeploymentName"
		values=[var.deployment_name]
	}
	filter {
		name="tag:kubernetes.io/role/internal-elb"
		values=["1"]
	}
	filter {
		name="vpc-id"
		values=[data.aws_vpc.current.id]
	}
}
data "aws_vpc" "current" {
	id=data.aws_eks_cluster.cluster.vpc_config[0].vpc_id
}