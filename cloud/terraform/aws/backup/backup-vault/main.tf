resource "aws_backup_vault" "this" {
	name=var.backup_vault_name
}
resource "aws_iam_role" "this" {
	assume_role_policy=jsonencode(
		{
			Statement=[
				{
					Action="sts:AssumeRole"
					Effect="Allow"
					Principal={
						Service="backup.amazonaws.com"
					}
				}
			]
			Version="2012-10-17"
		})
	name=var.aws_backup_service_assumed_iam_role_name
}
resource "aws_iam_role_policy_attachment" "this" {
	for_each=toset(
		[
			"arn:aws:iam::aws:policy/AWSBackupServiceRolePolicyForS3Backup",
			"arn:aws:iam::aws:policy/AWSBackupServiceRolePolicyForS3Restore",
			"arn:aws:iam::aws:policy/service-role/AWSBackupServiceRolePolicyForBackup",
			"arn:aws:iam::aws:policy/service-role/AWSBackupServiceRolePolicyForRestores"
		])
	policy_arn=each.key
	role=aws_iam_role.this.name
}