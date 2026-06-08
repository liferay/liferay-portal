#!/usr/bin/env bash

function _schema_name {
	local db_name

	db_name="$(_derive_db_name "$(basename "${1}")")"

	[[ ${db_name} != lportal ]] || return 1

	echo "${db_name#lportal_}"
}

function _psql_admin {
	local dbname="${1}"

	shift

	psql --host localhost --username postgres --dbname "${dbname}" --no-psqlrc "${@}"
}

function _drop_database {
	local worktree_path="${1}"

	command -v psql >/dev/null 2>&1 || return 0

	local name

	name="$(_schema_name "${worktree_path}")" || return 0

	_psql_admin postgres --command "DROP SCHEMA IF EXISTS \"${name}\" CASCADE" >&2 || true
	_psql_admin postgres --command "DROP OWNED BY \"${name}\"" >&2 || true
	_psql_admin postgres --command "DROP ROLE IF EXISTS \"${name}\"" >&2 || true
}

function _set_database {
	local name

	name="$(_schema_name "${WORKTREE_DIR}")" || return 0

	local file="${BUNDLES_DIR}/portal-ext.properties"
	local wizard_file="${BUNDLES_DIR}/portal-setup-wizard.properties"

	local existing_password

	existing_password="$(_get_property_from_files "jdbc\.default\.password" "" "${file}" "${wizard_file}")"

	_set_property "${file}" jdbc.default.driverClassName org.postgresql.Driver
	_set_property "${file}" jdbc.default.url "jdbc:postgresql://localhost/postgres?currentSchema=${name}"
	_set_property "${file}" jdbc.default.username "${name}"
	_set_property "${file}" jdbc.default.password "${existing_password}"

	command -v psql >/dev/null 2>&1 || return 0

	local create_role="CREATE ROLE \"${name}\" LOGIN"

	if [[ -n ${existing_password} ]]
	then
		create_role="${create_role} PASSWORD '${existing_password}'"
	fi

	_psql_admin postgres --tuples-only --no-align --command "SELECT 1 FROM pg_roles WHERE rolname = '${name}'" | grep --quiet 1 ||
		_psql_admin postgres --command "${create_role}" >&2 || true

	_psql_admin postgres --command "CREATE SCHEMA IF NOT EXISTS \"${name}\" AUTHORIZATION \"${name}\"" >&2 || true
}