#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source _common.sh

function main {
	local oauth_application_name=${1:-}

	if [[ -z ${oauth_application_name} ]]
	then
		echo "The OAuth application name was not provided." >&2

		exit 1
	fi

	cd ..

	touch .env

	sed --in-place --regexp-extended "/^OAUTH_CLIENT_(ID|SECRET)=/d" .env

	local client_id

	client_id=$(docker exec liferay cat "/opt/liferay/routes/default/${oauth_application_name}/${oauth_application_name}.oauth2.headless.server.client.id")

	local client_secret

	client_secret=$(docker exec liferay cat "/opt/liferay/routes/default/${oauth_application_name}/${oauth_application_name}.oauth2.headless.server.client.secret")

	{
		echo "OAUTH_CLIENT_ID=${client_id}"
		echo "OAUTH_CLIENT_SECRET=${client_secret}"
	} >> .env

	echo "The OAuth credentials were written to .env."
}

main "${@}"