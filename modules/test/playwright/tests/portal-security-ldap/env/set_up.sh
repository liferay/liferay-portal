#!/bin/bash

DEPENDENCIES_DIR_NAME=$(dirname "${BASH_SOURCE[0]}/../dependencies/")

echo DEPENDENCIES_DIR_NAME=${DEPENDENCIES_DIR_NAME}

source ${DEPENDENCIES_DIR_NAME}/../../../env/common.sh

function ldap_set_up {
	echo "Starting slapd:"
	/usr/local/libexec/slapd -F /usr/local/etc/slapd.d

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	local exampleCompanyLdif="${DEPENDENCIES_DIR_NAME}/exampleCompany.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${exampleCompanyLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	local adminLdif="${DEPENDENCIES_DIR_NAME}/admin.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${adminLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	local addUsersLdif="${DEPENDENCIES_DIR_NAME}/addUsers.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${addUsersLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	local addGroupsLdif="${DEPENDENCIES_DIR_NAME}/addGroups.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${addGroupsLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi
}

function main {
	default_set_up

	ldap_set_up
}

main "${@}"