#!/bin/bash

DEPENDENCIES_DIR_NAME=$(dirname "${BASH_SOURCE[0]}/../dependencies/")

echo DEPENDENCIES_DIR_NAME=${DEPENDENCIES_DIR_NAME}

source ${DEPENDENCIES_DIR_NAME}/../../../env/common.sh

function main {
	default_tear_down

	ldap_tear_down
}

function ldap_tear_down {
	local removeGroupsLdif="${DEPENDENCIES_DIR_NAME}/removeGroups.ldif"

	ldapdelete -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${removeGroupsLdif}

	local removeUsersLdif="${DEPENDENCIES_DIR_NAME}/removeUsers.ldif"

	ldapdelete -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${removeUsersLdif}

	kill -INT `cat /usr/local/var/run/slapd.pid`
}

main "${@}"