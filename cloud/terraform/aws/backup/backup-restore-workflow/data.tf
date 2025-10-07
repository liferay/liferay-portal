data "aws_caller_identity" "current" {
}
data "aws_eks_cluster" "cluster" {
	name=var.cluster_name
}
data "aws_iam_policy_document" "assume_role_with_web_identity" {
	statement {
		actions=["sts:AssumeRoleWithWebIdentity"]
		condition {
			test="StringEquals"
			values=["sts.amazonaws.com"]
			variable="${local.oidc_provider_url}:aud"
		}
		condition {
			test="StringEquals"
			values=["system:serviceaccount:${var.kubernetes_namespace}:${var.kubernetes_service_account_name}"]
			variable="${local.oidc_provider_url}:sub"
		}
		effect="Allow"
		principals {
			identifiers=[local.oidc_provider_arn]
			type="Federated"
		}
	}
}
data "aws_iam_policy_document" "liferay_backup_restore_policy_doc" {
	statement {
		actions=[
			"s3:DeleteObject",
			"s3:GetObject",
			"s3:ListBucket",
			"s3:PutObject"
		]
		effect="Allow"
		resources=[
			module.artifact_repository_s3_bucket.s3_bucket_arn,
			"${module.artifact_repository_s3_bucket.s3_bucket_arn}/*",
		]
		sid="AllowArtifactRepositoryAccess"
	}
	statement {
		actions=[
			"iam:PassRole"
		]
		effect="Allow"
		resources=[var.aws_backup_service_assumed_iam_role_arn]
		sid="AllowPassingRoleToAWSBackupService"
	}
	statement {
		actions=[
			"backup:DescribeRecoveryPoint",
			"backup:DescribeRestoreJob",
			"backup:GetRecoveryPointRestoreMetadata",
			"backup:ListRecoveryPointsByBackupVault",
			"backup:StartRestoreJob",
			"ec2:Describe*",
			"eks:DescribeCluster",
			"es:Describe*",
			"es:ListTags",
			"iam:Get*",
			"iam:List*",
			"rds:AddTagsToResource",
			"rds:DeleteDBInstance",
			"rds:DescribeDB*",
			"rds:ListTagsForResource",
			"rds:ModifyDBInstance",
			"rds:RestoreDBInstanceFromDBSnapshot",
			"s3:CreateBucket",
			"s3:DeleteBucket",
			"s3:DeleteObject",
			"s3:GetAccelerateConfiguration",
			"s3:GetBucket*",
			"s3:GetEncryptionConfiguration",
			"s3:GetLifecycleConfiguration",
			"s3:GetReplicationConfiguration",
			"s3:ListBucket",
			"s3:PutBucket*",
			"s3:PutEncryptionConfiguration"
		]
		effect="Allow"
		resources=["*"]
		sid="AllowRestoreWorkflowActions"
	}
	statement {
		actions=[
			"s3:GetObject",
			"s3:ListBucket",
			"s3:PutObject"
		]
		effect="Allow"
		resources=[
			var.terraform_state_bucket_arn,
			"${var.terraform_state_bucket_arn}/*",
		]
		sid="AllowTerraformStateBucketAccess"
	}
}
locals {
	oidc_provider_arn="arn:aws:iam::${data.aws_caller_identity.current.account_id}:oidc-provider/${local.oidc_provider_url}"
	oidc_provider_url=replace(data.aws_eks_cluster.cluster.identity[0].oidc[0].issuer, "https://", "")
}