#!/bin/bash

CURRENT_DIR_NAME=$(dirname ${BASH_SOURCE[0]})

echo CURRENT_DIR_NAME=${CURRENT_DIR_NAME}

source ${CURRENT_DIR_NAME}/../../../env/common.sh

function main {
	default_tear_down

	ldap_tear_down
}

function ldap_tear_down {
	local removeGroupsLdif="${CURRENT_DIR_NAME}/removeGroups.ldif"

	ldapdelete -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${removeGroupsLdif}

	local removeUsersLdif="${CURRENT_DIR_NAME}/removeUsers.ldif"

	ldapdelete -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${removeUsersLdif}

	kill -INT `cat /usr/local/var/run/slapd.pid`
}

main "${@}"