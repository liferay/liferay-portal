#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/_common.sh"

function get_remote_backend {
	local configuration_json_file="${1}"

	local bucket_name

	bucket_name=$(jq --raw-output '.tfstate.bucket_name' "${configuration_json_file}")

	cat <<EOF
	backend "gcs" {
		bucket="${bucket_name}"
	}
EOF
}

function get_tfstate_path_setting {
	echo "prefix=${1}"
}

function log_in {
	local configuration_json_file="${1}"

	local project_id

	project_id=$(jq --raw-output '.project_id' "${configuration_json_file}")

	export TF_VAR_project_id="${project_id}"

	if gcloud auth print-access-token --quiet &> /dev/null
	then
		echo "Reusing the active Google Cloud login."
	else
		echo "Attempting to login to your Google Cloud account."

		gcloud auth login
	fi

	if gcloud auth application-default print-access-token --quiet &> /dev/null
	then
		echo "Reusing the active Google Cloud application default credentials."
	else
		echo "Attempting to login to your Google Cloud account via application default credentials."

		gcloud auth application-default login
	fi

	echo "Using the Google Cloud project ${project_id}."
}

function update_kubeconfig {
	local cluster_terraform_stack="${2}"

	local membership_name

	membership_name=$(run_terraform "gcp" "${cluster_terraform_stack}" output -raw membership_name)

	local project_id

	project_id=$(run_terraform "gcp" "${cluster_terraform_stack}" output -raw project_id)

	gcloud container fleet memberships get-credentials \
		"${membership_name}" \
		--project "${project_id}"
}

function validate_config_json {
	local configuration_json_file="${1}"

	local required_keys=(
		".deployment_name"
		".project_id"
		".region"
	)

	if has_remote_tfstate "${configuration_json_file}"
	then
		required_keys+=(
			".tfstate.bucket_name"
		)
	fi

	check_config_json "${configuration_json_file}" "${required_keys[@]}"
}