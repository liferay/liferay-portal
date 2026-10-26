#!/bin/bash

function main {
	pushd ../../../../../

	touch "build.$(whoami).properties"

	if [[ $(grep -c "build.repository.private.password" "build.$(whoami).properties") == 0 ]]
	then
		local nexus_password

		nexus_password=$(op read "op://Analytics Cloud Team/nexus-private-ac/password")

		echo "build.repository.private.password=${nexus_password}" >> "build.$(whoami).properties"
	fi

	if [[ $(grep -c "build.repository.private.url" "build.$(whoami).properties") == 0 ]]
	then
		local nexus_url

		nexus_url=$(op read "op://Analytics Cloud Team/nexus-private-ac/website")

		echo "build.repository.private.url=${nexus_url}" >> "build.$(whoami).properties"
	fi

	if [[ $(grep -c "build.repository.private.username" "build.$(whoami).properties") == 0 ]]
	then
		local nexus_username

		nexus_username=$(op read "op://Analytics Cloud Team/nexus-private-ac/username")

		echo "build.repository.private.username=${nexus_username}" >> "build.$(whoami).properties"
	fi

	ant update-gradle-properties

	popd
}

main