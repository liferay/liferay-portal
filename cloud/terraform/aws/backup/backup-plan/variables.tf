variable "backup_plan_name" {
	default="liferay-backup"
}
variable "backup_rules" {
	default=[
		{
			retention_days=30
			rule_name="DailyBackups"
			schedule="cron(0 5 * * ? *)"
			start_window_minutes=60
		}
	]
	type=list(
		object(
			{
				retention_days=number
				rule_name=string
				schedule=string
				start_window_minutes=number
			}
		)
	)
}
variable "backup_selection_name" {
	default="by-backup-tag"
}
variable "backup_service_assumed_role_arn" {
	type=string
}
variable "backup_vault_name" {
	default="liferay-backup"
}
variable "region" {
	default="us-west-2"
}
