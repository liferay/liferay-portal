#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function main {
	local fail=0
	local pass=0
	local script

	script=$(cd "$(dirname "${0}")/.." && pwd)/overlay-sync.sh

	export -f rclone

	_run_test "${script}" _test_exits_with_error_when_argument_count_is_wrong
	_run_test "${script}" _test_exits_with_error_when_no_bucket_env_var_is_set
	_run_test "${script}" _test_passes_plain_path_to_rclone_without_include
	_run_test "${script}" _test_splits_glob_pattern_into_path_and_include_filter
	_run_test "${script}" _test_strips_leading_slashes_from_target_path
	_run_test "${script}" _test_strips_wildcard_and_passes_include_for_wildcard_path
	_run_test "${script}" _test_target_path_is_prefixed_with_temp
	_run_test "${script}" _test_uses_liferay_overlay_bucket_name_when_set

	echo ""
	echo "Results: ${pass} passed, ${fail} failed."

	if [ "${fail}" -eq 0 ]
	then
		return 0
	fi

	return 1
}

function rclone {
	echo "rclone ${*}"
}

function _run_test {
	local script="${1}"
	local test_function="${2}"

	local description

	description=$(echo "${test_function}" | sed "s/^_test_//; s/_/ /g")

	local exit_code=0

	"${test_function}" "${script}" || exit_code="${?}"

	if [ "${exit_code}" -eq 0 ]
	then
		echo "PASS: ${description}."

		pass=$((pass + 1))
	else
		echo "FAIL: ${description}."

		fail=$((fail + 1))
	fi
}

function _test_exits_with_error_when_argument_count_is_wrong {
	if [[ "$(bash "${1}" gcs /source 2>&1)" == *"Usage:"* ]]
	then
		return 0
	fi

	return 1
}

function _test_exits_with_error_when_no_bucket_env_var_is_set {
	if [[ "$(unset LIFERAY_OVERLAY_BUCKET_NAME; bash "${1}" gcs source/path dest/path 2>&1)" == *"Overlay bucket does not exist"* ]]
	then
		return 0
	fi

	return 1
}

function _test_passes_plain_path_to_rclone_without_include {
	local output

	output=$(LIFERAY_OVERLAY_BUCKET_NAME="test-bucket" bash "${1}" gcs source/path dest/path 2>&1)

	if [[ "${output}" == *"rclone copy :gcs,env_auth=true:test-bucket/source/path"* ]] && [[ "${output}" != *"--include"* ]]
	then
		return 0
	fi

	return 1
}

function _test_splits_glob_pattern_into_path_and_include_filter {
	local output

	output=$(LIFERAY_OVERLAY_BUCKET_NAME="test-bucket" bash "${1}" gcs "source/path/*.jar" dest/path 2>&1)

	if [[ "${output}" == *"rclone copy :gcs,env_auth=true:test-bucket/source/path"* ]] && [[ "${output}" == *"--include *.jar"* ]]
	then
		return 0
	fi

	return 1
}

function _test_strips_leading_slashes_from_target_path {
	if [[ "$(LIFERAY_OVERLAY_BUCKET_NAME="test-bucket" bash "${1}" gcs source/path /mnt/liferay/patching 2>&1)" == *"/temp/mnt/liferay/patching"* ]]
	then
		return 0
	fi

	return 1
}

function _test_strips_wildcard_and_passes_include_for_wildcard_path {
	local output

	output=$(LIFERAY_OVERLAY_BUCKET_NAME="test-bucket" bash "${1}" gcs "source/path/*" dest/path 2>&1)

	if [[ "${output}" == *"rclone copy :gcs,env_auth=true:test-bucket/source/path"* ]] && [[ "${output}" == *"--include *"* ]]
	then
		return 0
	fi

	return 1
}

function _test_target_path_is_prefixed_with_temp {
	if [[ "$(LIFERAY_OVERLAY_BUCKET_NAME="test-bucket" bash "${1}" gcs source/path osgi/configs 2>&1)" == *"/temp/osgi/configs"* ]]
	then
		return 0
	fi

	return 1
}

function _test_uses_liferay_overlay_bucket_name_when_set {
	if [[ "$(LIFERAY_OVERLAY_BUCKET_NAME="test-bucket" bash "${1}" gcs source/path dest/path 2>&1)" == *":gcs,env_auth=true:test-bucket/source/path"* ]]
	then
		return 0
	fi

	return 1
}

main "${@}"