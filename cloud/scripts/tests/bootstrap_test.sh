#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

source "$(cd "$(dirname "${0}")/.." && pwd)/bootstrap.sh"

function curl {
	local arg
	local capture=""
	local output=""
	local url=""

	for arg in "${@}"
	do
		if [[ ${capture} == yes ]]
		then
			capture=""
			output="${arg}"

			continue
		fi

		if [[ ${arg} == --output ]]
		then
			capture="yes"
		fi

		if [[ ${arg} == https://* ]]
		then
			url="${arg}"
		fi
	done

	if [[ ${url} == https://storage.googleapis.com/* ]]
	then
		printf '%s' "${CURL_STUB_METADATA_JSON-}"

		return 0
	fi

	if [[ ${url} == *.sha256 ]]
	then
		if [[ ${CURL_STUB_SIDECAR_FAIL-} == true ]]
		then
			return 22
		fi

		printf '%s' "${CURL_STUB_SIDECAR_BODY-}" > "${output}"

		return 0
	fi

	printf '%s' "${CURL_STUB_TARBALL_BODY-}" > "${output}"
}

function main {
	local fail=0
	local pass=0

	_run_test _test_aborts_with_empty_checksum_file
	_run_test _test_aborts_with_invalid_checksum_format
	_run_test _test_aborts_with_mismatched_checksum
	_run_test _test_aborts_with_missing_checksum_file
	_run_test _test_ignores_sidecar_objects_when_resolving_latest
	_run_test _test_succeeds_with_valid_checksum

	echo ""
	echo "Results: ${pass} passed, ${fail} failed."

	if [ ${fail} -eq 0 ]
	then
		return 0
	fi

	return 1
}

function tar {
	return 0
}

function _make_metadata_json {
	local with_sidecar="${1:-}"

	local tarball_item='{"name": "bootstrap/liferay-aws-bootstrap/liferay-aws-bootstrap-1.2.3.tar.gz", "updated": "2026-01-01T00:00:00Z"}'

	if [[ -n ${with_sidecar} ]]
	then
		echo "{\"items\": [${tarball_item}, {\"name\": \"bootstrap/liferay-aws-bootstrap/liferay-aws-bootstrap-1.2.3.tar.gz.sha256\", \"updated\": \"2026-01-01T00:00:01Z\"}]}"

		return 0
	fi

	echo "{\"items\": [${tarball_item}]}"
}

function _make_sidecar_body {
	local tarball_body="${1}"

	local digest

	digest=$(printf '%s' "${tarball_body}" | _sha256 | awk '{print $1}')

	echo "${digest}  liferay-aws-bootstrap-1.2.3.tar.gz"
}

function _run_download_test {
	local metadata_json="${1}"
	local sidecar_body="${2}"
	local sidecar_fail="${3:-}"
	local tarball_body="${4:-tarball-bytes}"

	local tmpdir

	tmpdir=$(mktemp -d)

	local exit_code=0
	local output

	output=$( \
		cd "${tmpdir}" && \
			CURL_STUB_METADATA_JSON="${metadata_json}" \
			CURL_STUB_SIDECAR_BODY="${sidecar_body}" \
			CURL_STUB_SIDECAR_FAIL="${sidecar_fail}" \
			CURL_STUB_TARBALL_BODY="${tarball_body}" \
				_download_and_extract_files "" aws latest 2>&1) || exit_code="${?}"

	rm -rf "${tmpdir}"

	echo "${exit_code}"
	echo "${output}"
}

function _run_test {
	local test_function="${1}"

	local description

	description=$(echo "${test_function}" | sed "s/^_test_//; s/_/ /g")

	local exit_code=0

	"${test_function}" || exit_code="${?}"

	if [ ${exit_code} -eq 0 ]
	then
		echo "PASS: ${description}."

		pass=$((pass + 1))
	else
		echo "FAIL: ${description}."

		fail=$((fail + 1))
	fi
}

function _test_aborts_with_empty_checksum_file {
	local exit_code
	local output
	local result

	result=$(_run_download_test "$(_make_metadata_json)" "")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"Invalid expected checksum format"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_invalid_checksum_format {
	local exit_code
	local output
	local result

	result=$(_run_download_test "$(_make_metadata_json)" "not-hex  whatever")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"Invalid expected checksum format"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_mismatched_checksum {
	local exit_code
	local output
	local result

	result=$(_run_download_test "$(_make_metadata_json)" "0000000000000000000000000000000000000000000000000000000000000000  whatever")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"Checksum verification failed"* ]]
	then
		return 0
	fi

	return 1
}

function _test_aborts_with_missing_checksum_file {
	local exit_code
	local output
	local result

	result=$(_run_download_test "$(_make_metadata_json)" "" true)
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -ne 0 ] && [[ ${output} == *"Unable to download checksum"* ]]
	then
		return 0
	fi

	return 1
}

function _test_ignores_sidecar_objects_when_resolving_latest {
	local exit_code
	local output
	local result

	result=$(_run_download_test "$(_make_metadata_json with_sidecar)" "$(_make_sidecar_body "tarball-bytes")")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -eq 0 ] && [[ ${output} == liferay-aws-bootstrap-1.2.3 ]]
	then
		return 0
	fi

	return 1
}

function _test_succeeds_with_valid_checksum {
	local exit_code
	local output
	local result

	result=$(_run_download_test "$(_make_metadata_json)" "$(_make_sidecar_body "tarball-bytes")")
	exit_code=$(echo "${result}" | head -n 1)
	output=$(echo "${result}" | tail -n +2)

	if [ ${exit_code} -eq 0 ] && [[ ${output} == liferay-aws-bootstrap-1.2.3 ]]
	then
		return 0
	fi

	return 1
}

main "${@}"