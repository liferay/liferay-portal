output "ack_prometheusservice_role_arn" {
	value=try(aws_iam_role.ack_prometheusservice[0].arn, "")
}
output "cluster_secret_store_provider" {
	value=local.cluster_secret_store_provider
}
output "deployment_context" {
	value=local.deployment_context
}
output "external_secrets_role_arn" {
	value=aws_iam_role.external_secrets.arn
}
output "keda_role_arn" {
	value=try(aws_iam_role.keda[0].arn, "")
}
output "keda_service_account_namespace" {
	value=local.keda_enabled ? var.keda_config.namespace : ""
}
output "kubernetes_endpoint_cidrs" {
	value=sort([for subnet in data.aws_subnet.cluster : subnet.cidr_block])
}
output "marketplace_volume_handle" {
	value="${data.aws_efs_file_system.marketplace.id}::${aws_efs_access_point.marketplace.id}"
}