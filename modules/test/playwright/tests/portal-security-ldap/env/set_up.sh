#!/bin/bash

CURRENT_DIR_NAME=$(dirname ${BASH_SOURCE[0]})

echo CURRENT_DIR_NAME=${CURRENT_DIR_NAME}

source ${CURRENT_DIR_NAME}/../../../env/common.sh

function ldap_set_up {
	###########################################################################
	# Start: steps can be removed with next CI release
	###########################################################################

	# Copy over entire correct file
	local ciSlapdLdifLocation="/usr/local/etc/openldap/slapd.ldif"
	local modifiedSlapdLdif="${CURRENT_DIR_NAME}/modifiedSlapd.ldif"

	cp ${modifiedSlapdLdif} ${ciSlapdLdifLocation}

	# Delete slapd.d dir if it exists
	if [ -d /usr/local/etc/slapd.d ]
	then
		echo "deleting slapd.d"
		rm -rf /usr/local/etc/slapd.d
	else
		echo "slapd.d does not exist"
	fi

	mkdir /usr/local/etc/slapd.d

	# Check if slapd.d now exists
	if [ -d /usr/local/etc/slapd.d ]
	then
		echo "slapd.d exists"
	else
		echo "slapd.d still does not exist"
	fi

	# Import configuration database

	echo "Performing slapadd:"
	/usr/local/sbin/slapadd -n 0 -F /usr/local/etc/slapd.d -l ${ciSlapdLdifLocation}

	###########################################################################
	# End: steps can be removed with next CI release
	###########################################################################

	# Start SLAPD
	echo "Starting slapd:"
	/usr/local/libexec/slapd -F /usr/local/etc/slapd.d

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	# Add Example Company via ldif file
	local exampleCompanyLdif="${CURRENT_DIR_NAME}/exampleCompany.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${exampleCompanyLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	# Add admin via ldif file
	local adminLdif="${CURRENT_DIR_NAME}/admin.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${adminLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	# Add users via ldif file
	local addUsersLdif="${CURRENT_DIR_NAME}/addUsers.ldif"

	ldapadd -cx -D "cn=admin,dc=example,dc=com" -w "secret" -f ${addUsersLdif}

	if [ $? -ne 0 ]; then
		echo "Command failed with exit status $?"
	else
		echo "Command succeeded"
	fi

	# Add groups via ldif file
	local addGroupsLdif="${CURRENT_DIR_NAME}/addGroups.ldif"

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