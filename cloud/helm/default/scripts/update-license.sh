#!/bin/bash

LICENSE_URL="${LIFERAY_SUBSCRIPTION_HOST}"
LICENSE_URL+="${LIFERAY_SUBSCRIPTION_PATH}"
LICENSE_URL+="${LIFERAY_SUBSCRIPTION_ID}"

LICENSE_RESPONSE=$( \
	curl "${LICENSE_URL}" \
		--fail \
		--header "Accept: application/json" \
		--header "Authorization: Bearer ${LIFERAY_SUBSCRIPTION_AUTH_TOKEN}" \
		--show-error \
		--silent \
)

if [ $? -ne 0 ]; then
	echo "License fetch failed."

	exit 1
fi

FETCHED_LICENSE_BASE64=$( \
	echo "${LICENSE_RESPONSE}" | jq --raw-output '.license' \
)

if [ -z "${FETCHED_LICENSE_BASE64}" ]; then
	echo "Fetched license value empty or invalid."

	exit 1
fi

CURRENT_LICENSE_BASE64=$( \
	kubectl get secret "${LICENSE_SECRET_NAME}" \
		-o 'go-template={{index .data "license.xml"}}' \
)

if [ "${FETCHED_LICENSE_BASE64}" = "${CURRENT_LICENSE_BASE64}" ]; then
	echo "Current license still valid. Exiting without update."

	exit 0
fi

echo "Updating license."

PATCH='{"data": {"license.xml": "'"${FETCHED_LICENSE_BASE64}"'"}}'

kubectl patch secret "${LICENSE_SECRET_NAME}" --patch="${PATCH}"

if [ $? -ne 0 ]; then
	echo "License update failed."

	exit 1
fi

echo "License updated."
