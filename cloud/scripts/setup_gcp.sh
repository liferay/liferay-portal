#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

trap "_recover_kubectl_context \${?}" ERR

_GCP_DEPLOYMENT_NAME=""
_GCP_PROJECT_ID=""

_SCRIPTS_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

_ROOT_CLOUD_DIR=$(cd "${_SCRIPTS_DIR}/.." && pwd)

function main {
	if [ ${#} -eq 0 ]
	then
		echo "Usage: ${0} <configuration-json-file>" >&2
		echo "" >&2
		echo "See cloud/scripts/config.json.example_gcp for a sample." >&2

		exit 1
	fi

	_check_utils gcloud jq kubectl terraform

	_check_terraform_version "1.10.0"

	_validate_config_json "${1}"

	_generate_tfvars "${1}" "${_SCRIPTS_DIR}/global_terraform.tfvars"

	_GCP_DEPLOYMENT_NAME="$(jq --raw-output '.variables.deployment_name' "${1}")"
	_GCP_PROJECT_ID="$(jq --raw-output '.variables.project_id' "${1}")"

	echo "Attempting to login to your Google Cloud account via application default credentials."

	gcloud auth application-default login

	local bucket_name=""
	local region=""

	local terraform_args=()

	while IFS= read -r terraform_arg
	do
		terraform_args+=("${terraform_arg}")
	done < <(_get_terraform_apply_args "${1}")

	if jq --exit-status '.variables.tfstate_bucket_name' "${1}" &> /dev/null
	then
		bucket_name="$(jq --raw-output '.variables.tfstate_bucket_name' "${1}")"
		region="$(jq --raw-output '.variables.region' "${1}")"

		_create_tfstate_bucket "${bucket_name}" "${region}" "${_GCP_PROJECT_ID}"
	fi

	_set_up_gcp_gke "${bucket_name}" "${_GCP_DEPLOYMENT_NAME}" "${region}" "${terraform_args[@]}"

	_set_up_gcp_gitops "${bucket_name}" "${_GCP_DEPLOYMENT_NAME}" "${region}" "${terraform_args[@]}"
}

function _check_terraform_version {
	local found_version

	found_version=$(terraform --version | awk '/^Terraform v/ {print $2; exit}')
	found_version="${found_version#v}"

	local required_version="${1}"

	local lowest_version

	lowest_version=$(printf "%s\n%s\n" "${required_version}" "${found_version}" | sort --version-sort | head -n 1)

	if [[ ${lowest_version} != ${required_version} ]]
	then
		echo "The installed Terraform version ${found_version} is older than ${required_version}." >&2

		exit 1
	fi
}

function _check_utils {
	for util in "${@}"
	do
		if (! command -v "${util}" &> /dev/null)
		then
			echo "The utility ${util} is not installed."

			exit 1
		fi
	done
}

function _configure_gcs_bucket {
	local bucket_name="${1}"
	local key_name="tfstate-${bucket_name}"
	local project_id="${3}"
	local region="${2}"

	if ! gcloud kms keyrings describe \
		"${key_name}" \
		--location "${region}" \
		--project "${project_id}" \
		&> /dev/null
	then
		_log "Creating KMS keyring ${key_name}."

		gcloud kms keyrings create \
			"${key_name}" \
			--location "${region}" \
			--project "${project_id}"

		_log "KMS keyring ${key_name} was created successfully."
	else
		_log "KMS keyring ${key_name} already exists. Skipping creation process."
	fi

	if ! gcloud kms keys describe \
		"${key_name}" \
		--keyring "${key_name}" \
		--location "${region}" \
		--project "${project_id}" \
		&> /dev/null
	then
		_log "Creating KMS key ${key_name}."

		gcloud kms keys create \
			"${key_name}" \
			--keyring "${key_name}" \
			--location "${region}" \
			--project "${project_id}" \
			--purpose "encryption"

		_log "KMS key ${key_name} was created successfully."
	else
		_log "KMS key ${key_name} already exists. Skipping creation process."
	fi

	local service_agent

	service_agent="$(gcloud storage service-agent --project "${project_id}")"

	gcloud kms keys add-iam-policy-binding \
		"${key_name}" \
		--keyring "${key_name}" \
		--location "${region}" \
		--member "serviceAccount:${service_agent}" \
		--project "${project_id}" \
		--role "roles/cloudkms.cryptoKeyEncrypterDecrypter" \
		> /dev/null

	gcloud storage buckets update \
		"gs://${bucket_name}" \
		--default-encryption-key "projects/${project_id}/locations/${region}/keyRings/${key_name}/cryptoKeys/${key_name}" \
		--project "${project_id}" \
		--retention-period "90d"
}

function _create_tfstate_bucket {
	local bucket_name="${1}"
	local project_id="${3}"
	local region="${2}"

	if ! gcloud storage buckets describe "gs://${bucket_name}" --project "${project_id}" &> /dev/null
	then
		_log "Creating bucket ${bucket_name}."

		_create_gcs_bucket "${bucket_name}" "${region}" "${project_id}"

		_log "Bucket ${bucket_name} was created successfully."
	else
		_log "Bucket ${bucket_name} already exists. Skipping creation process."
	fi

	_log "Configuring bucket ${bucket_name}."

	_configure_gcs_bucket "${bucket_name}" "${region}" "${project_id}"

	_log "Bucket ${bucket_name} was configured successfully."
}

function _create_gcs_bucket {
	local bucket_name="${1}"
	local project_id="${3}"
	local region="${2}"

	gcloud storage buckets create \
		"gs://${bucket_name}" \
		--location "${region}" \
		--project "${project_id}" \
		--public-access-prevention \
		--uniform-bucket-level-access \
		1> /dev/null

	gcloud storage buckets update \
		"gs://${bucket_name}" \
		--project "${project_id}" \
		--versioning
}

function _generate_tfvars {
	local configuration_json_file="${1}"
	local tfvars_file="${2}"

	echo "Generating ${tfvars_file} from ${configuration_json_file}."

	local tfvars_content

	tfvars_content=$(
		jq --raw-output '.variables
		| to_entries[]
		| if (.value | type) == "string"
		  then
		  	"\(.key) = \"\(.value)\""
		  elif (.value | type) == "array" or (.value | type) == "object"
		  then
		  	"\(.key) = \(.value | @json)"
		  else
		  	"\(.key) = \(.value)"
		  end' "${configuration_json_file}")

	if [[ -z ${tfvars_content} ]]
	then
		echo "The \"variables\" object in the configuration JSON file is empty. You will be prompted for all required variables."

		> "${tfvars_file}"
	else
		echo "${tfvars_content}" > "${tfvars_file}"
	fi

	echo "${tfvars_file} was generated successfully."
}

function _get_terraform_apply_args {
	local auto_approve="false"

	local configuration_json_file="${1}"

	if jq --exit-status '.options.auto_approve' "${configuration_json_file}" > /dev/null
	then
		auto_approve=$(jq --raw-output '.options.auto_approve' "${configuration_json_file}")
	fi

	local apply_args=(
		"-var-file=${_SCRIPTS_DIR}/global_terraform.tfvars")

	if [[ ${auto_approve} == true ]]
	then
		apply_args+=("-auto-approve")
	fi

	if jq --exit-status '.options.parallelism | numbers' "${configuration_json_file}" > /dev/null
	then
		local parallelism

		parallelism=$(jq --raw-output '.options.parallelism' "${configuration_json_file}")

		apply_args+=("-parallelism=${parallelism}")
	fi

	printf '%s\n' "${apply_args[@]}"
}

function _log {
	echo "[Tfstate bucket configuration] ${1}"
}

function _popd {
	popd > /dev/null
}

function _pushd {
	pushd "${1}" > /dev/null
}

function _recover_kubectl_context {
	local exit_code="${1}"

	if [[ -z ${_GCP_DEPLOYMENT_NAME:-} ]] || [[ ${_GCP_DEPLOYMENT_NAME} = null ]] || [[ -z ${_GCP_PROJECT_ID:-} ]] || [[ ${_GCP_PROJECT_ID} = null ]]
	then
		exit "${exit_code}"
	fi

	echo "Unable to apply Terraform. Attempting to recover the kubectl context via fleet membership ${_GCP_DEPLOYMENT_NAME}-membership." >&2

	if ! gcloud \
		container \
		fleet \
		memberships \
		describe \
		"${_GCP_DEPLOYMENT_NAME}-membership" \
		--project "${_GCP_PROJECT_ID}" \
		> /dev/null 2>&1
	then
		echo "Fleet membership ${_GCP_DEPLOYMENT_NAME}-membership does not exist. No kubectl context can be recovered." >&2

		exit "${exit_code}"
	fi

	if ! gcloud \
		container \
		fleet \
		memberships \
		get-credentials \
		"${_GCP_DEPLOYMENT_NAME}-membership" \
		--project "${_GCP_PROJECT_ID}"
	then
		echo "Unable to recover the kubectl context for fleet membership ${_GCP_DEPLOYMENT_NAME}-membership." >&2

		exit "${exit_code}"
	fi

	echo "Recovered the kubectl context via fleet membership ${_GCP_DEPLOYMENT_NAME}-membership." >&2

	exit "${exit_code}"
}

function _set_up_gcp_gitops {
	local bucket_name="${1}"
	local deployment_name="${2}"
	local region="${3}"

	_pushd "${_ROOT_CLOUD_DIR}/terraform/gcp/gitops"

	echo "Setting up the Google GCP GitOps infrastructure."

	_terraform_init_and_apply "./platform" "gitops/platform" "${bucket_name}" "${deployment_name}" "${region}" "${@:4}"

	_terraform_init_and_apply "./resources" "gitops/resources" "${bucket_name}" "${deployment_name}" "${region}" "${@:4}"

	echo "Google GCP GitOps infrastructure setup complete."

	_popd
}

function _set_up_gcp_gke {
	local bucket_name="${1}"
	local deployment_name="${2}"
	local region="${3}"

	_pushd "${_ROOT_CLOUD_DIR}/terraform/gcp/gke"

	echo "Setting up the Google GKE cluster."

	_terraform_init_and_apply "." "gke" "${bucket_name}" "${deployment_name}" "${region}" "${@:4}"

	gcloud auth login

	gcloud \
		container \
		fleet \
		memberships \
		get-credentials \
		"$(terraform output -raw membership_name)" \
		--project "$(terraform output -raw project_id)"

	echo "Google GKE cluster setup complete."

	_popd
}

function _terraform_init_and_apply {
	local bucket_name="${3}"
	local deployment_name="${4}"
	local folder_separator="${2}"
	local region="${5}"

	_pushd "${1}"

	if [[ -n ${bucket_name} ]]
	then
		terraform init \
			-backend-config="bucket=${bucket_name}" \
			-backend-config="prefix=${deployment_name}/${region}/${folder_separator}" \
			-upgrade
	else
		cat > backend_override.tf <<EOF
terraform {
	backend "local" {}
}
EOF
		terraform init -upgrade
	fi

	terraform apply "${@:6}"

	_popd
}

function _validate_config_json {
	local configuration_json_file="${1}"

	if [[ ! -f ${configuration_json_file} ]]
	then
		echo "Configuration JSON file ${configuration_json_file} does not exist." >&2

		exit 1
	fi

	if ! jq empty "${configuration_json_file}" &> /dev/null
	then
		echo "Configuration JSON file ${configuration_json_file} is not valid JSON." >&2

		exit 1
	fi

	if ! jq --exit-status '.variables | objects' "${configuration_json_file}" > /dev/null
	then
		echo "The configuration JSON file must contain a root object named \"variables\"." >&2

		exit 1
	fi
}

main "${@}"