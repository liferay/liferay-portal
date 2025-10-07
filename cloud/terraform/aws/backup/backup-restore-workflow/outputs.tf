output "artifact_repository_bucket_id" {
	value=module.artifact_repository_s3_bucket.s3_bucket_id
}
output "liferay_backup_restore_iam_role_arn" {
	value=aws_iam_role.this.arn
}