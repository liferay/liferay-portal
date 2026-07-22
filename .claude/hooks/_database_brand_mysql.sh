#!/usr/bin/env bash

function _drop_database {
	local worktree_path="${1}"
	local bundles_dir="${2:-}"

	command -v mysql >/dev/null 2>&1 || return 0

	local db_name

	db_name="$(_derive_db_name "$(basename "${worktree_path}")")"

	[[ ${db_name} != lportal ]] || return 0

	local user=root
	local password=""

	if [[ -n ${bundles_dir} ]]
	then
		user="$(_get_property_from_files "jdbc\.default\.username" root "${bundles_dir}/portal-ext.properties" "${bundles_dir}/portal-setup-wizard.properties")"
		password="$(_get_property_from_files "jdbc\.default\.password" "" "${bundles_dir}/portal-ext.properties" "${bundles_dir}/portal-setup-wizard.properties")"
	fi

	local mysql_args=(--user "${user}")

	if [[ -n ${password} ]]
	then
		mysql_args+=(--password="${password}")
	fi

	mysql "${mysql_args[@]}" --execute "DROP DATABASE IF EXISTS ${db_name};" >&2 || true
}

function _set_database {
	local db_name

	db_name="$(_derive_db_name "$(basename "${WORKTREE_DIR}")")"

	local file="${BUNDLES_DIR}/portal-ext.properties"
	local wizard_file="${BUNDLES_DIR}/portal-setup-wizard.properties"

	local existing_user existing_password

	existing_user="$(_get_property_from_files "jdbc\.default\.username" root "${file}" "${wizard_file}")"
	existing_password="$(_get_property_from_files "jdbc\.default\.password" "" "${file}" "${wizard_file}")"

	_set_property "${file}" jdbc.default.driverClassName com.mysql.cj.jdbc.Driver
	_set_property "${file}" jdbc.default.url "jdbc:mysql://localhost/${db_name}?characterEncoding=UTF-8&dontTrackOpenResources=true&holdResultsOpenOverStatementClose=true&serverTimezone=GMT&useFastDateParsing=false&useUnicode=true"
	_set_property "${file}" jdbc.default.username "${existing_user}"
	_set_property "${file}" jdbc.default.password "${existing_password}"

	command -v mysql >/dev/null 2>&1 || return 0

	local mysql_args=()

	if [[ -n ${existing_password} ]]
	then
		mysql_args+=(--password="${existing_password}")
	fi

	mysql_args+=(--user "${existing_user}")

	mysql "${mysql_args[@]}" --execute "CREATE DATABASE IF NOT EXISTS ${db_name} CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" >&2 || true
}