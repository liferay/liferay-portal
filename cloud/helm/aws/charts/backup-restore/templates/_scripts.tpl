{{- define "liferayAWSBackupRestore.script.applyTerraformModule" -}}
#!/bin/sh

set -eu

function main {
	cp /mnt/.git-credentials /tmp/.git-credentials

	git config --global credential.helper "store --file /tmp/.git-credentials"
	git config --global user.email "{{ .Values.git.user.emailAddress }}"
	git config --global user.name "{{ .Values.git.user.name }}"

	git pull origin {{ .Values.git.repository.branch }}

	echo '{{ "{{" }}inputs.parameters.tfvars-content}}' > {{ .Values.tfvarsOverrideFileName }}

	git add {{ .Values.tfvarsOverrideFileName }}

	if ! git diff --staged --quiet
	then
		git commit --message "{{ "{{" }}inputs.parameters.commit-message}}"

		git push origin HEAD:{{ .Values.git.repository.branch }}
	fi

	terraform init -input=false

	terraform apply -auto-approve -input=false
}

main
{{- end -}}

{{- define "liferayAWSBackupRestore.script.checkoutGitRepository" -}}
#!/bin/sh

set -eu

function main {
	cp /mnt/.git-credentials /tmp/.git-credentials

	git config --global credential.helper "store --file /tmp/.git-credentials"

	git \
		clone \
		--branch "{{ .Values.git.repository.branch }}" \
		--depth 1 \
		--filter blob:none \
		--no-checkout \
		"{{ .Values.git.repository.url }}" \
		/src

	cd /src

	git sparse-checkout set --no-cone "{{ .Values.git.repository.paths.sparseCheckout }}"

	git checkout
}

main
{{- end -}}

{{- define "liferayAWSBackupRestore.script.getDependenciesModuleOutputs" -}}
#!/bin/sh

set -eu

function main {
	terraform init -input=false

	terraform plan -detailed-exitcode -input=false

	local db_restore_snapshot_identifier

	db_restore_snapshot_identifier=$( \
		terraform \
			output \
			-raw \
			db_restore_snapshot_identifier 2>/dev/null || echo "")

	if [ -n "${db_restore_snapshot_identifier}" ]
	then
		echo "Terraform output \"db_restore_snapshot_identifier\" is not empty. A restore may already be in progress." >&2

		exit 1
	fi

	if [ $(terraform output -raw is_restoring) = "true" ];
	then
		echo "Terraform output \"is_restoring\" is set to \"true\". A restore may already be in progress." >&2

		exit 1
	fi

	terraform output -raw data_active > /tmp/data-active.txt
	terraform output -raw data_inactive > /tmp/data-inactive.txt
	terraform output -raw s3_bucket_id_active > /tmp/s3-bucket-id-active.txt
	terraform output -raw s3_bucket_id_inactive > /tmp/s3-bucket-id-inactive.txt
}

main
{{- end -}}

{{- define "liferayAWSBackupRestore.script.getPeerRecoveryPoints" -}}
#!/bin/sh

set -eu

function get_recovery_point_arn_by_type {
	local recovery_points_json="${2}"
	local resource_type="${1}"

	local filtered_recovery_points_json

	filtered_recovery_points_json=$( \
		echo \
			"${recovery_points_json}" \
			| jq --arg resource_type "${resource_type}" "[.[] | select(.ResourceType == \$resource_type)]")

	local filtered_recovery_points_length

	filtered_recovery_points_length=$(echo "${filtered_recovery_points_json}" | jq "length")

	if [ "${filtered_recovery_points_length}" -ne 1 ]
	then
		echo "A single recovery point of type \"${resource_type}\" was expected, but ${filtered_recovery_points_length} were found." >&2

		return 1
	fi

	echo "${filtered_recovery_points_json}" | jq --raw-output ".[0].RecoveryPointArn"
}

function main {
	local recovery_point_details

	recovery_point_details=$( \
		aws \
			backup \
			describe-recovery-point \
			--backup-vault-name "{{ .Values.awsBackupService.vaultName }}" \
			--recovery-point-arn "{{ "{{" }}workflow.parameters.recovery-point-arn}}")

	local creation_date

	creation_date=$(echo "${recovery_point_details}" | jq --raw-output ".CreationDate")

	if [ -z "${creation_date}" ] || [ "${creation_date}" = "null" ]
	then
		echo "The provided recovery point ARN has no creation date." >&2

		return 1
	fi

	local creation_date_timestamp

	creation_date_timestamp=$(date --date "${creation_date}" +%s)

	local by_created_after

	by_created_after=$(date --date @$((creation_date_timestamp - 1)) --iso-8601=seconds)

	local by_created_before

	by_created_before=$(date --date @$((creation_date_timestamp + 1)) --iso-8601=seconds)

	local peer_recovery_points

	peer_recovery_points=$( \
		aws \
			backup \
			list-recovery-points-by-backup-vault \
			--backup-vault-name "{{ .Values.awsBackupService.vaultName }}" \
			--by-created-after "${by_created_after}" \
			--by-created-before "${by_created_before}" \
			| jq --arg creation_date "${creation_date}" "[.RecoveryPoints[] | select(.CreationDate == \$creation_date)]")

	local rds_recovery_point_arn

	rds_recovery_point_arn=$(get_recovery_point_arn_by_type "RDS" "${peer_recovery_points}")

	local rds_snapshot_id

	rds_snapshot_id=$( \
		echo \
			"${rds_recovery_point_arn}" \
			| awk --field-separator "snapshot:" "{print \$2}")

	if [ -z "${rds_snapshot_id}" ]
	then
		echo "The RDS snapshot ID could not be parsed from ${rds_recovery_point_arn}." >&2

		exit 1
	fi

	echo "${rds_snapshot_id}" > /tmp/rds-snapshot-id.txt

	local s3_recovery_point_arn

	s3_recovery_point_arn=$(get_recovery_point_arn_by_type "S3" "${peer_recovery_points}")

	echo "${s3_recovery_point_arn}" > /tmp/s3-recovery-point-arn.txt
}

main
{{- end -}}

{{- define "liferayAWSBackupRestore.script.restoreS3Bucket" -}}
#!/bin/sh

set -eu

function main {
	local restore_job_id

	restore_job_id=$( \
		aws \
			backup \
			start-restore-job \
			--iam-role-arn "{{ .Values.awsBackupService.assumedIamRoleArn }}" \
			--metadata "DestinationBucketName={{ "{{" }}inputs.parameters.s3-bucket-id}},NewBucket=false" \
			--recovery-point-arn "{{ "{{" }}inputs.parameters.s3-recovery-point-arn}}" \
			--resource-type "S3" \
			| jq --raw-output ".RestoreJobId")

	local timeout

	timeout=$(( $(date +%s) + {{ .Values.awsBackupService.restoreWaitTimeoutSeconds }} ))

	while [ $(date +%s) -lt ${timeout} ]
	do
		local restore_job_status_json

		restore_job_status_json=$( \
			aws \
				backup \
				describe-restore-job \
				--restore-job-id "${restore_job_id}")

		local restore_job_status

		restore_job_status=$(echo "${restore_job_status_json}" | jq --raw-output ".Status")

		if [ "${restore_job_status}" = "ABORTED" ] || [ "${restore_job_status}" = "FAILED" ]
		then
			local restore_job_status_message

			restore_job_status_message=$( \
				echo \
					"${restore_job_status_json}" \
					| jq --raw-output ".StatusMessage")

			echo "The restore job \"${restore_job_id}\" failed with status \"${restore_job_status}\": ${restore_job_status_message}." >&2

			exit 1
		elif [ "${restore_job_status}" = "COMPLETED" ]
		then
			exit 0
		else
			echo "The current restore job status is \"${restore_job_status}\"."

			sleep 30
		fi
	done

	echo "The restore timed out." >&2

	exit 1
}

main
{{- end -}}