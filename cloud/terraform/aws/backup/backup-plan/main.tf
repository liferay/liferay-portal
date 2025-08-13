resource "aws_backup_plan" "liferay-backup" {
	dynamic "rule" {
		for_each=var.backup_rules
		content {
			lifecycle {
				delete_after=rule.value.retention_days
			}
			rule_name=rule.value.rule_name
			schedule=rule.value.schedule
			start_window=rule.value.start_window_minutes
			target_vault_name=var.backup_vault_name
		}
	}
	name=var.backup_plan_name
}
resource "aws_backup_selection" "by_backup_tag" {
	iam_role_arn=var.backup_service_assumed_role_arn
	name=var.backup_selection_name
	plan_id=aws_backup_plan.liferay-backup.id
	selection_tag {
		key="Backup"
		type="STRINGEQUALS"
		value="true"
	}
}