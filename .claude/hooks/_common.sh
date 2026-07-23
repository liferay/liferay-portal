#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function _atomic_write {
	local file="${1}"

	cat > "${file}.tmp"

	mv "${file}.tmp" "${file}"
}

function _derive_db_name {
	local dir_name="${1}"

	local suffix="${dir_name#liferay-portal}"

	suffix="${suffix#-}"

	if [[ -z ${suffix} ]]
	then
		echo lportal

		return
	fi

	suffix="$(echo "${suffix}" | tr "[:upper:]" "[:lower:]" | _sed --regexp-extended "s/[^a-z0-9]+/_/g; s/^_+|_+\$//g")"
	suffix="${suffix:0:56}"

	echo "lportal_${suffix}"
}

function _die {
	echo "${*}" >&2

	exit 1
}

function _find_app_server_parent_dir {
	local project_dir="${1}"

	local default_props="${project_dir}/app.server.properties"
	local user_props="${project_dir}/app.server.${USER}.properties"

	local raw=""

	if [[ -f ${user_props} ]]
	then
		raw="$(grep --extended-regexp "^[[:space:]]*app\.server\.parent\.dir=" "${user_props}" | tail --lines=1 | _sed "s/^[[:space:]]*app\.server\.parent\.dir=//")"
	fi

	if [[ -z ${raw} && -f ${default_props} ]]
	then
		raw="$(grep --extended-regexp "^[[:space:]]*app\.server\.parent\.dir=" "${default_props}" | tail --lines=1 | _sed "s/^[[:space:]]*app\.server\.parent\.dir=//")"
	fi

	[[ -n ${raw} ]] || return 1

	raw="${raw//\$\{project.dir\}/${project_dir}}"

	(cd "$(dirname "${raw}")" 2>/dev/null && printf "%s/%s\n" "$(pwd)" "$(basename "${raw}")") || return 1
}

function _find_tomcat_dir {
	local bundles_dir="${1}"

	local candidate latest=""

	for candidate in "${bundles_dir}"/tomcat-*
	do
		[[ -d ${candidate} ]] || continue

		if [[ -z ${latest} || ${candidate} > ${latest} ]]
		then
			latest="${candidate}"
		fi
	done

	[[ -n ${latest} ]] || return 1

	echo "${latest}"
}

function _get_property {
	local file="${1}"
	local key="${2}"
	local default="${3:-}"

	local value="${default}"

	if [[ -f ${file} ]] && grep --extended-regexp --quiet "^[[:space:]]*${key}=" "${file}"
	then
		value="$(grep --extended-regexp "^[[:space:]]*${key}=" "${file}" | tail --lines=1 | _sed --regexp-extended "s/^[[:space:]]*${key}=[[:space:]]*//; s/[[:space:]]+\$//")"
	fi

	echo "${value}"
}

function _get_property_from_files {
	local key="${1}"
	local default="${2}"

	shift 2

	local file

	for file in "${@}"
	do
		if [[ -f ${file} ]] && grep --extended-regexp --quiet "^[[:space:]]*${key}=" "${file}"
		then
			grep --extended-regexp "^[[:space:]]*${key}=" "${file}" | tail --lines=1 | _sed --regexp-extended "s/^[[:space:]]*${key}=[[:space:]]*//; s/[[:space:]]+\$//"

			return
		fi
	done

	echo "${default}"
}

function _sed {
	local arg in_place=0

	for arg in "${@}"
	do
		[[ ${arg} == --in-place ]] && in_place=1
	done

	if [[ ${in_place} -eq 1 ]]
	then
		local file="${*: -1}"

		if [[ -L ${file} ]]
		then
			local dereferenced="${file}.dereferenced"

			cp "${file}" "${dereferenced}" 2>/dev/null || : > "${dereferenced}"

			rm -f "${file}"

			mv "${dereferenced}" "${file}"
		fi
	fi

	if [[ $(uname) == Darwin ]]
	then
		local args=()

		for arg in "${@}"
		do
			if [[ ${arg} == --expression ]]
			then
				args+=(-e)
			elif [[ ${arg} == --in-place ]]
			then
				args+=(-i "")
			elif [[ ${arg} == --regexp-extended ]]
			then
				args+=(-E)
			else
				args+=("${arg}")
			fi
		done

		sed "${args[@]}"
	else
		sed "${@}"
	fi
}

function _source_database_brand {
	local brand

	brand="$(echo "${LIFERAY_PROVISION_DATABASE_BRAND:-mysql}" | tr "[:upper:]" "[:lower:]")"

	if [[ ${brand} == *psql* || ${brand} == *postgres* ]]
	then
		brand=psql
	elif [[ ${brand} == *mysql* ]]
	then
		brand=mysql
	else
		_die "The \"LIFERAY_PROVISION_DATABASE_BRAND\" value must contain one of \"mysql\", \"psql\", or \"postgres\" (got \"${brand}\")."
	fi

	source "_database_brand_${brand}.sh"

	local function_name

	for function_name in _drop_database _set_database
	do
		[[ $(type -t "${function_name}") == function ]] ||
			_die "\"_database_brand_${brand}.sh\" must implement the \"${function_name}\" function."
	done
}

_source_database_brand