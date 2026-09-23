resource "aws_iam_policy" "crossplane_iam_boundary" {
	name="${var.deployment_name}-crossplane-iam-boundary"
	policy=data.aws_iam_policy_document.crossplane_iam_boundary.json
}
resource "aws_iam_role" "ack_prometheusservice" {
	assume_role_policy=data.aws_iam_policy_document.irsa["ack_prometheusservice"].json
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-ack-prometheusservice"
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
resource "aws_iam_role" "keda" {
	assume_role_policy=data.aws_iam_policy_document.irsa["keda"].json
	count=local.keda_enabled ? 1 : 0
	name="${var.deployment_name}-keda"
}
resource "aws_iam_role_policy" "ack_prometheusservice" {
	count=var.observability_config.enabled ? 1 : 0
	name="amp-rule-groups"
	policy=data.aws_iam_policy_document.ack_prometheusservice.json
	role=aws_iam_role.ack_prometheusservice[0].id
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
resource "aws_iam_role_policy" "keda" {
	count=local.keda_enabled ? 1 : 0
	name="amp-query"
	policy=data.aws_iam_policy_document.keda.json
	role=aws_iam_role.keda[0].id
}