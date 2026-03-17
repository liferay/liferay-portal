resource "grafana_data_source" "amp" {
	is_default=true
	json_data_encoded=jsonencode(
		{
			sigv4_assume_role_arn=var.grafana_workspace_role_arn
			sigv4_auth=true
			sigv4_auth_type="workspace-iam-role"
			sigv4_region=var.region
		})
	name="amp"
	type="prometheus"
	url=var.prometheus_workspace_endpoint
}