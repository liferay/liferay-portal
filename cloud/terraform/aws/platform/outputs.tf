output "ack_prometheusservice_role_arn" {
	value=try(aws_iam_role.ack_prometheusservice[0].arn, "")
}
output "alloy_role_arn" {
	value=try(aws_iam_role.alloy[0].arn, "")
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
output "grafana_role_arn" {
	value=try(aws_iam_role.grafana[0].arn, "")
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
output "prometheus_workspace_endpoint" {
	value=try(aws_prometheus_workspace.amp[0].prometheus_endpoint, "")
}
output "prometheus_workspace_id" {
	value=try(aws_prometheus_workspace.amp[0].id, "")
}
output "rds_exporter_role_arn" {
	value=try(aws_iam_role.rds_exporter[0].arn, "")
}