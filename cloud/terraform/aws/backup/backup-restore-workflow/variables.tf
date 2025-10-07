variable "artifact_repository_bucket_prefix" {
	default="argo-workflows-artifact-repository-"
}
variable "aws_backup_service_assumed_iam_role_arn" {
	type=string
}
variable "cluster_name" {
	type=string
}
variable "kubernetes_namespace" {
	default="liferay-system"
}
variable "kubernetes_service_account_name" {
	default="liferay-aws-backup-restore"
}
variable "region" {
	type=string
}
variable "tags" {
	default={}
	type=map(string)
}
variable "terraform_state_bucket_arn" {
	type=string
}