module "artifact_repository_s3_bucket" {
	bucket_prefix=var.artifact_repository_bucket_prefix
	server_side_encryption_configuration={
		rule={
			apply_server_side_encryption_by_default={
				sse_algorithm="AES256"
			}
		}
	}
	source="terraform-aws-modules/s3-bucket/aws"
	tags=var.tags
	version="~> 4.1.1"
	versioning={
		enabled=true
	}
}
resource "aws_iam_policy" "this" {
	name="${var.cluster_name}-${var.kubernetes_service_account_name}-policy"
	policy=data.aws_iam_policy_document.liferay_backup_restore_policy_doc.json
	tags=var.tags
}
resource "aws_iam_role" "this" {
	assume_role_policy=data.aws_iam_policy_document.assume_role_with_web_identity.json
	name="${var.cluster_name}-${var.kubernetes_service_account_name}-role"
	tags=var.tags
}
resource "aws_iam_role_policy_attachment" "this" {
	policy_arn=aws_iam_policy.this.arn
	role=aws_iam_role.this.name
}