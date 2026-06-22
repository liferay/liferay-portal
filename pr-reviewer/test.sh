#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

source run.sh

function main {
	_test_classify_failure
	_test_extract_last_json_block
	_test_format_tokens
	_test_get_indefinite_article_for_number
	_test_is_transient_error
	_test_salvage_chance
	_test_salvage_violations

	echo ""
	echo "Ran ${_TESTS_RUN} tests, ${_TESTS_FAILED} failed."

	[[ ${_TESTS_FAILED} -eq 0 ]]
}

function _assert_equals {
	local actual=${1}
	local expected=${2}
	local message=${3}

	((++_TESTS_RUN))

	local depth=1

	while [[ ${FUNCNAME[depth]} != _test_* ]]
	do
		((++depth))
	done

	local unit=${FUNCNAME[depth]#_test_}

	if [[ ${actual} == "${expected}" ]]
	then
		echo "PASS [${unit}] ${message}"
	else
		((++_TESTS_FAILED))

		echo "FAIL [${unit}] ${message} (test.sh:${BASH_LINENO[depth - 1]}: expected \"${expected}\", got \"${actual}\")"
	fi
}

function _assert_extract_last_json_block {
	local input=${1}
	local expected=${2}
	local message=${3}

	local block

	block=$(printf "%s" "${input}" | _extract_last_json_block)

	local chance=NONE

	if [[ ! -z ${block} ]]
	then
		chance=$(printf "%s" "${block}" | jq --raw-output '.chance // "NONE"')
	fi

	_assert_equals "${chance}" "${expected}" "${message}"
}

function _test_classify_failure {
	_assert_equals "$(_classify_failure '{"is_error": false}' 'I could not review.')" no_verdict "Classify a missing verdict."
	_assert_equals "$(_classify_failure '{"is_error": true}' 'API Error: 500')" api_error "Classify an API error."
	_assert_equals "$(_classify_failure '' '')" empty "Classify an empty result."
	_assert_equals "$(_classify_failure '{"is_error": false}' '{"chance": 5, "violations": ["x" "y"]}')" malformed_json "Classify malformed JSON."
}

function _test_extract_last_json_block {
	local input='{"chance": 30, "violations": []}'

	_assert_extract_last_json_block "${input}" 30 "Extract a plain verdict."

	input='{"chance": 15, "violations": ["z"]}
Let me know if you need more.'

	_assert_extract_last_json_block "${input}" 15 "Extract before trailing prose."

	input='```json
{"chance": 50, "violations": ["f"]}
```'

	_assert_extract_last_json_block "${input}" 50 "Extract from a fenced code block."

	input='Closing `}` at line 96.
{"chance": 40, "violations": ["x"]}'

	_assert_extract_last_json_block "${input}" 40 "Extract past a stray closing brace."

	input='The opening `{` of the method.
{"chance": 25, "violations": ["y"]}'

	_assert_extract_last_json_block "${input}" 25 "Extract past a stray opening brace."

	input='I could not complete the review.'

	_assert_extract_last_json_block "${input}" NONE "Ignore prose with no JSON."

	input='{"chance": 5, "violations": ["uses "quotes" badly"]}'

	_assert_extract_last_json_block "${input}" NONE "Reject malformed JSON."

	input='Example: {"key": "value"}.
Final:
{"chance": 60, "violations": []}'

	_assert_extract_last_json_block "${input}" 60 "Skip an incidental object."
}

function _test_format_tokens {
	_assert_equals "$(_format_tokens 500)" 500 "Format a token count below a thousand."
	_assert_equals "$(_format_tokens 48301)" 48k "Format a token count in thousands."
}

function _test_get_indefinite_article_for_number {
	_assert_equals "$(_get_indefinite_article_for_number 40)" a "Choose \"a\" for forty."
	_assert_equals "$(_get_indefinite_article_for_number 8)" an "Choose \"an\" for eight."
}

function _test_is_transient_error {
	_assert_equals "$(_is_transient_error '{"api_error_status": 400, "is_error": true}' && echo yes || echo no)" no "Treat a 400 as not transient."
	_assert_equals "$(_is_transient_error '{"api_error_status": 429, "is_error": true}' && echo yes || echo no)" yes "Treat a 429 rate limit as transient."
	_assert_equals "$(_is_transient_error '{"api_error_status": 529, "is_error": true}' && echo yes || echo no)" yes "Treat a 529 overload as transient."
	_assert_equals "$(_is_transient_error '{"is_error": false}' && echo yes || echo no)" no "Treat a successful reply as not transient."
}

function _test_salvage_chance {
	_assert_equals "$(printf "%s" '**chance**: 46, details below' | _salvage_chance)" 46 "Salvage a chance from a Markdown reply."
	_assert_equals "$(printf "%s" '{"chance": 72, "violations": ["uses "quotes""]}' | _salvage_chance)" 72 "Salvage a chance from malformed JSON."
	_assert_equals "$(printf "%s" 'I could not review.' | _salvage_chance)" "" "Salvage nothing when there is no chance."
}

function _test_salvage_violations {
	_assert_equals "$(printf "%s" 'I could not review.' | _salvage_violations)" "" "Salvage nothing when there are no violations."
	_assert_equals "$(printf "%s" 'Key findings: one issue found.
{"chance": 35, "violations": ["x"]}' | _salvage_violations)" 'Key findings: one issue found.' "Salvage the analysis above a JSON verdict."
	_assert_equals "$(printf "%s" '{"chance": 5, "violations": ["bad "quotes" here", "second"]}' | _salvage_violations)" '"bad "quotes" here", "second"' "Salvage the violations text from malformed JSON."
	_assert_equals "$(printf "%s" '**violations**:
1. First violation
2. Second violation' | _salvage_violations)" '1. First violation
2. Second violation' "Salvage violations from a Markdown reply."
}

_TESTS_FAILED=0
_TESTS_RUN=0

main "${@}"