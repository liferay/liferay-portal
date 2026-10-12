data "aws_caller_identity" "current" {
}
data "aws_efs_file_system" "marketplace" {
	creation_token="${var.deployment_name}-marketplace"
}
data "aws_eks_cluster" "eks" {
	name="${var.deployment_name}-eks"
}
data "aws_iam_openid_connect_provider" "eks" {
	url=data.aws_eks_cluster.eks.identity[0].oidc[0].issuer
}
data "aws_iam_policy_document" "ack_prometheusservice" {
	statement {
		actions=[
			"aps:CreateRuleGroupsNamespace",
			"aps:DeleteRuleGroupsNamespace",
			"aps:DescribeRuleGroupsNamespace",
			"aps:ListRuleGroupsNamespaces",
			"aps:ListTagsForResource",
			"aps:PutRuleGroupsNamespace",
			"aps:TagResource",
			"aps:UntagResource",
		]
		effect="Allow"
		resources=[
			"arn:${local.partition}:aps:${var.region}:${local.account_id}:rulegroupsnamespace/*",
			"arn:${local.partition}:aps:${var.region}:${local.account_id}:workspace/*",
		]
	}
}
data "aws_iam_policy_document" "alloy" {
	statement {
		actions=[
			"aps:GetLabels",
			"aps:GetMetricMetadata",
			"aps:GetSeries",
			"aps:QueryMetrics",
			"aps:RemoteWrite",
		]
		effect="Allow"
		resources=aws_prometheus_workspace.amp[*].arn
	}
}
data "aws_iam_policy_document" "alloy_cloudwatch_rds" {
	statement {
		actions=[
			"cloudwatch:GetMetricData",
			"cloudwatch:GetMetricStatistics",
			"cloudwatch:ListMetrics",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"rds:DescribeDBInstances",
			"rds:ListTagsForResource",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["tag:GetResources"]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "crossplane_data_backup" {
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
		actions=["iam:GetRole"]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["iam:PassRole"]
		condition {
			test="StringEquals"
			values=["backup.amazonaws.com"]
			variable="iam:PassedToService"
		}
		effect="Allow"
		resources=["arn:${local.partition}:iam::${local.account_id}:role${local.crossplane_iam_path}*"]
	}
	statement {
		actions=["kms:CreateGrant"]
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
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["kms:DescribeKey"]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "crossplane_data_cloudwatchlogs" {
	statement {
		actions=[
			"logs:CreateLogGroup",
			"logs:DeleteLogGroup",
			"logs:DeleteRetentionPolicy",
			"logs:DescribeLogGroups",
			"logs:ListTagsForResource",
			"logs:PutRetentionPolicy",
			"logs:TagResource",
			"logs:UntagResource",
		]
		effect="Allow"
		resources=["arn:${local.partition}:logs:${var.region}:${local.account_id}:log-group:*"]
	}
	statement {
		actions=[
			"logs:DeleteResourcePolicy",
			"logs:DescribeResourcePolicies",
			"logs:PutResourcePolicy",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_iam_policy_document" "crossplane_data_ec2" {
	statement {
		actions=[
			"ec2:AuthorizeSecurityGroupIngress",
			"ec2:CreateNetworkInterface",
			"ec2:CreateSecurityGroup",
			"ec2:CreateTags",
			"ec2:DeleteNetworkInterface",
			"ec2:DeleteSecurityGroup",
			"ec2:DeleteTags",
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
data "aws_iam_policy_document" "crossplane_data_kms" {
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
data "aws_iam_policy_document" "crossplane_data_opensearch" {
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
			"es:GetUpgradeStatus",
			"es:ListDomainNames",
			"es:ListTags",
			"es:RemoveTags",
			"es:UpdateDomainConfig",
			"es:UpgradeDomain",
			"kms:CreateGrant",
			"kms:DescribeKey",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["iam:CreateServiceLinkedRole"]
		condition {
			test="StringEquals"
			values=["opensearchservice.amazonaws.com"]
			variable="iam:AWSServiceName"
		}
		effect="Allow"
		resources=["arn:${local.partition}:iam::${local.account_id}:role/aws-service-role/opensearchservice.amazonaws.com/*"]
	}
}
data "aws_iam_policy_document" "crossplane_data_rds" {
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
			"rds:RemoveTagsFromResource",
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
	statement {
		actions=["iam:CreateServiceLinkedRole"]
		condition {
			test="StringEquals"
			values=["rds.amazonaws.com"]
			variable="iam:AWSServiceName"
		}
		effect="Allow"
		resources=["arn:${local.partition}:iam::${local.account_id}:role/aws-service-role/rds.amazonaws.com/*"]
	}
}
data "aws_iam_policy_document" "crossplane_data_s3" {
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
			"s3:ListBucketVersions",
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
		resources=["arn:${local.partition}:s3:::*"]
	}
	statement {
		actions=[
			"s3:DeleteObject",
			"s3:DeleteObjectVersion",
			"s3:GetObject",
			"s3:PutObject",
		]
		effect="Allow"
		resources=["arn:${local.partition}:s3:::*/*"]
	}
}
data "aws_iam_policy_document" "crossplane_iam" {
	statement {
		actions=[
			"iam:GetAccessKeyLastUsed",
			"iam:GetLoginProfile",
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
			"iam:ListMFADevices",
			"iam:ListPolicyVersions",
			"iam:ListRolePolicies",
			"iam:ListSSHPublicKeys",
			"iam:ListServiceSpecificCredentials",
			"iam:ListSigningCertificates",
			"iam:ListUserPolicies",
			"iam:ListVirtualMFADevices",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"iam:AttachRolePolicy",
			"iam:AttachUserPolicy",
			"iam:CreateRole",
			"iam:CreateUser",
			"iam:DeleteUserPolicy",
			"iam:DetachRolePolicy",
			"iam:DetachUserPolicy",
			"iam:PutUserPolicy",
		]
		condition {
			test="StringEquals"
			values=[aws_iam_policy.crossplane_iam_boundary.arn]
			variable="iam:PermissionsBoundary"
		}
		effect="Allow"
		resources=[
			"arn:${local.partition}:iam::${local.account_id}:role${local.crossplane_iam_path}*",
			"arn:${local.partition}:iam::${local.account_id}:user${local.crossplane_iam_path}*",
		]
	}
	statement {
		actions=[
			"iam:CreateAccessKey",
			"iam:DeleteAccessKey",
			"iam:DeleteLoginProfile",
			"iam:DeleteRole",
			"iam:DeleteUser",
			"iam:TagRole",
			"iam:TagUser",
			"iam:UntagRole",
			"iam:UntagUser",
			"iam:UpdateAccessKey",
			"iam:UpdateAssumeRolePolicy",
			"iam:UpdateUser",
		]
		effect="Allow"
		resources=[
			"arn:${local.partition}:iam::${local.account_id}:role${local.crossplane_iam_path}*",
			"arn:${local.partition}:iam::${local.account_id}:user${local.crossplane_iam_path}*",
		]
	}
	statement {
		actions=[
			"iam:CreatePolicy",
			"iam:CreatePolicyVersion",
			"iam:DeletePolicy",
			"iam:DeletePolicyVersion",
			"iam:TagPolicy",
			"iam:UntagPolicy",
		]
		effect="Allow"
		resources=["arn:${local.partition}:iam::${local.account_id}:policy${local.crossplane_iam_path}*"]
	}
}
data "aws_iam_policy_document" "crossplane_iam_boundary" {
	statement {
		actions=[
			"aws-marketplace:BatchMeterUsage",
			"aws-marketplace:RegisterUsage",
			"backup-storage:*",
			"backup:*",
			"cloudwatch:GetMetricData",
			"ec2:Describe*",
			"ecr:BatchCheckLayerAvailability",
			"ecr:BatchGetImage",
			"ecr:GetAuthorizationToken",
			"ecr:GetDownloadUrlForLayer",
			"events:DeleteRule",
			"events:DescribeRule",
			"events:DisableRule",
			"events:EnableRule",
			"events:ListRules",
			"events:ListTargetsByRule",
			"events:PutRule",
			"events:PutTargets",
			"events:RemoveTargets",
			"kms:CreateGrant",
			"kms:Decrypt",
			"kms:DescribeKey",
			"kms:Encrypt",
			"kms:GenerateDataKey*",
			"kms:ReEncrypt*",
			"kms:RetireGrant",
			"rds:*",
			"s3:*",
			"tag:GetResources",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=["iam:PassRole"]
		condition {
			test="StringEquals"
			values=["backup.amazonaws.com"]
			variable="iam:PassedToService"
		}
		effect="Allow"
		resources=["arn:${local.partition}:iam::${local.account_id}:role${local.crossplane_iam_path}*"]
	}
}
data "aws_iam_policy_document" "external_secrets" {
	statement {
		actions=[
			"secretsmanager:DescribeSecret",
			"secretsmanager:GetSecretValue",
		]
		effect="Allow"
		resources=[
			"arn:${local.partition}:secretsmanager:${var.region}:${local.account_id}:secret:liferay-certificates-*",
			"arn:${local.partition}:secretsmanager:${var.region}:${local.account_id}:secret:liferay-credentials-*",
			"arn:${local.partition}:secretsmanager:${var.region}:${local.account_id}:secret:liferay-licenses-*",
		]
	}
}
data "aws_iam_policy_document" "external_secrets_marketplace_registry" {
	statement {
		actions=["ecr:GetAuthorizationToken"]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"ecr:BatchCheckLayerAvailability",
			"ecr:BatchGetImage",
			"ecr:GetDownloadUrlForLayer",
		]
		effect="Allow"
		resources=["arn:${local.partition}:ecr:us-east-1:709825985650:repository/*"]
	}
}
data "aws_iam_policy_document" "irsa" {
	for_each=local.irsa_service_accounts
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider}:aud"
		}
		condition {
			test="StringEquals"
			values=["system:serviceaccount:${each.value}"]
			variable="${local.oidc_provider}:sub"
		}
		effect="Allow"
		principals {
			identifiers=[data.aws_iam_openid_connect_provider.eks.arn]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "keda" {
	statement {
		actions=[
			"aps:GetLabels",
			"aps:GetMetricMetadata",
			"aps:GetSeries",
			"aps:QueryMetrics",
		]
		effect="Allow"
		resources=["arn:${local.partition}:aps:${var.region}:${local.account_id}:workspace/*"]
	}
}
data "aws_iam_policy_document" "rds_exporter" {
	statement {
		actions=[
			"cloudwatch:GetMetricData",
			"cloudwatch:GetMetricStatistics",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"ec2:DescribeAccountAttributes",
			"ec2:DescribeInstanceTypes",
		]
		effect="Allow"
		resources=["*"]
	}
	statement {
		actions=[
			"rds:DescribeDBClusters",
			"rds:DescribeDBInstances",
			"rds:ListTagsForResource",
		]
		effect="Allow"
		resources=["*"]
	}
}
data "aws_partition" "current" {
}
data "aws_subnet" "cluster" {
	for_each=data.aws_eks_cluster.eks.vpc_config[0].subnet_ids
	id=each.value
}