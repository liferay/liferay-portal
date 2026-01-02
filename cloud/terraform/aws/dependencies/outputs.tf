output "ci_uploader_access_key_id" {
  value=aws_iam_access_key.ci_uploader.id
}
output "ci_uploader_access_key_secret" {
  sensitive=true
  value=aws_iam_access_key.ci_uploader.secret
}
output "cluster_name" {
	value=var.cluster_name
}
output "data_active" {
	value=var.data_active
}
output "data_inactive" {
	value=local.data_inactive
}
output "db_restore_snapshot_identifier" {
	value=var.db_restore_snapshot_identifier
}
output "deployment_namespace" {
	value=var.deployment_namespace
}
output "is_restoring" {
	value=var.is_restoring
}
output "liferay_sa_role" {
	value=var.liferay_sa_role_arn
}
output "region" {
	value=var.region
}
output "s3_bucket_id_active" {
	value=local.bucket_active.s3_bucket_id
}
output "s3_bucket_id_extensions" {
	value=local.bucket_extensions.s3_bucket_id
}
output "s3_bucket_id_inactive" {
	value=local.bucket_inactive.s3_bucket_id
}