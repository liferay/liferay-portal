resource "aws_grafana_workspace" "amg" {
	account_access_type="CURRENT_ACCOUNT"
	authentication_providers=["AWS_SSO"]
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-amg-workspace"
	permission_type="SERVICE_MANAGED"
	role_arn=aws_iam_role.grafana[0].arn
}
resource "aws_grafana_workspace_api_key" "amg_api_key" {
	count=var.observability_config.enabled ? 1 : 0
	key_name="terraform-api-key-${time_rotating.grafana_api_key[0].id}"
	key_role="ADMIN"
	seconds_to_live=3600
	workspace_id=aws_grafana_workspace.amg[0].id
}
resource "aws_iam_role" "grafana" {
	assume_role_policy=jsonencode(
		{
			Statement=[
				{
					Action="sts:AssumeRole",
					Effect="Allow",
					Principal={
						Service="grafana.amazonaws.com"
					}
				}
			]
			Version="2012-10-17"
		})
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-grafana-role"
}
resource "aws_iam_role_policy" "amp_access" {
	count=var.observability_config.enabled ? 1 : 0
	name="${var.deployment_name}-amg-amp-access-policy"
	policy=jsonencode(
		{
			Statement=[
				{
					Action=[
						"aps:DescribeWorkspace",
						"aps:GetLabels",
						"aps:GetMetricMetadata",
						"aps:GetSeries",
						"aps:ListWorkspaces",
						"aps:QueryMetrics"
					],
					Effect="Allow",
					Resource="*"
				}
			]
			Version="2012-10-17"
		})
	role=aws_iam_role.grafana[0].id
}
resource "aws_prometheus_workspace" "amp" {
	alias="${var.deployment_name}-amp-workspace"
	count=var.observability_config.enabled ? 1 : 0
}
resource "time_rotating" "grafana_api_key" {
	count=var.observability_config.enabled ? 1 : 0
	rotation_hours=1
}