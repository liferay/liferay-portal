resource "aws_backup_plan" "this" {
	dynamic "rule" {
		content {
			lifecycle {
				delete_after=rule.value.retention_days
			}
			rule_name=rule.value.rule_name
			schedule=rule.value.schedule
			start_window=rule.value.start_window_minutes
			target_vault_name=var.backup_vault_name
		}
		for_each=var.backup_rules
	}
	name=var.backup_plan_name
}
resource "aws_backup_selection" "this" {
	condition {
		string_equals {
			key="aws:ResourceTag/Active"
			value="true"
		}
		string_equals {
			key="aws:ResourceTag/Backup"
			value="true"
		}
		string_equals {
			key="aws:ResourceTag/DeploymentName"
			value=var.deployment_name
		}
	}
	iam_role_arn=var.aws_backup_service_assumed_iam_role_arn
	name=var.backup_selection_name
	plan_id=aws_backup_plan.this.id
	resources=["*"]
}