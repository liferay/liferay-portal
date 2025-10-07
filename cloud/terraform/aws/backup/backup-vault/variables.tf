variable "aws_backup_service_assumed_iam_role_name" {
	default="liferay-aws-backup-service-role"
}
variable "backup_vault_name" {
	default="liferay-backup"
}
variable "region" {
	type=string
}