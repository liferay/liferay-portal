resource "aws_iam_policy" "crossplane_iam_boundary" {
	name="${var.deployment_name}-crossplane-iam-boundary"
	policy=data.aws_iam_policy_document.crossplane_iam_boundary.json
}
resource "aws_iam_role" "ack_prometheusservice" {
	assume_role_policy=data.aws_iam_policy_document.irsa["ack_prometheusservice"].json
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-ack-prometheusservice"
}
resource "aws_iam_role" "alloy" {
	assume_role_policy=data.aws_iam_policy_document.irsa["alloy"].json
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-alloy"
}
resource "aws_iam_role" "crossplane_data" {
	assume_role_policy=data.aws_iam_policy_document.irsa["crossplane_data"].json
	name="${var.deployment_name}-crossplane-data"
}
resource "aws_iam_role" "crossplane_iam" {
	assume_role_policy=data.aws_iam_policy_document.irsa["crossplane_iam"].json
	name="${var.deployment_name}-crossplane-iam"
}
resource "aws_iam_role" "external_secrets" {
	assume_role_policy=data.aws_iam_policy_document.irsa["external_secrets"].json
	name="${var.deployment_name}-external-secrets"
}
resource "aws_iam_role" "grafana" {
	assume_role_policy=data.aws_iam_policy_document.irsa["grafana"].json
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-grafana"
}
resource "aws_iam_role" "keda" {
	assume_role_policy=data.aws_iam_policy_document.irsa["keda"].json
	count=local.keda_enabled ? 1 : 0
	name="${var.deployment_name}-keda"
}
resource "aws_iam_role" "rds_exporter" {
	assume_role_policy=data.aws_iam_policy_document.irsa["rds_exporter"].json
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-rds-exporter"
}
resource "aws_iam_role_policy" "ack_prometheusservice" {
	count=var.observability_config.enabled ? 1 : 0
	name="amp-rule-groups"
	policy=data.aws_iam_policy_document.ack_prometheusservice.json
	role=aws_iam_role.ack_prometheusservice[0].id
}
resource "aws_iam_role_policy" "alloy" {
	count=var.observability_config.enabled ? 1 : 0
	name="amp-remote-write"
	policy=data.aws_iam_policy_document.alloy.json
	role=aws_iam_role.alloy[0].id
}
resource "aws_iam_role_policy" "alloy_cloudwatch_rds" {
	count=var.observability_config.enabled ? 1 : 0
	name="cloudwatch-rds"
	policy=data.aws_iam_policy_document.alloy_cloudwatch_rds.json
	role=aws_iam_role.alloy[0].id
}
resource "aws_iam_role_policy" "crossplane_data" {
	for_each=local.crossplane_data_policies
	name=each.key
	policy=each.value
	role=aws_iam_role.crossplane_data.id
}
resource "aws_iam_role_policy" "crossplane_iam" {
	name="iam"
	policy=data.aws_iam_policy_document.crossplane_iam.json
	role=aws_iam_role.crossplane_iam.id
}
resource "aws_iam_role_policy" "external_secrets" {
	count=local.default_secrets_manager_enabled ? 1 : 0
	name="secrets-manager"
	policy=data.aws_iam_policy_document.external_secrets.json
	role=aws_iam_role.external_secrets.id
}
resource "aws_iam_role_policy" "external_secrets_marketplace_registry" {
	name="marketplace-registry"
	policy=data.aws_iam_policy_document.external_secrets_marketplace_registry.json
	role=aws_iam_role.external_secrets.id
}
resource "aws_iam_role_policy" "keda" {
	count=local.keda_enabled ? 1 : 0
	name="amp-query"
	policy=data.aws_iam_policy_document.keda.json
	role=aws_iam_role.keda[0].id
}
resource "aws_iam_role_policy" "rds_exporter" {
	count=var.observability_config.enabled ? 1 : 0
	name="rds-describe"
	policy=data.aws_iam_policy_document.rds_exporter.json
	role=aws_iam_role.rds_exporter[0].id
}
resource "aws_iam_role_policy_attachment" "grafana" {
	count=var.observability_config.enabled ? 1 : 0
	policy_arn="arn:${local.partition}:iam::aws:policy/AmazonPrometheusQueryAccess"
	role=aws_iam_role.grafana[0].name
}