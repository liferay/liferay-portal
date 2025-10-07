/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export async function addContactRoleNameByEmailByProject(
	accountKey: string,
	emailURI: string,
	firstName: string,
	lastName: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	roleName: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/contacts/by-email-address/${emailURI}/roles?contactRoleNames=${roleName}&firstName=${firstName}&lastName=${lastName}`,
		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
			method: 'PUT',
		}
	);

	if (!response.ok) {
		throw new Error('Error', {cause: response.status});
	}

	return response;
}

export async function deleteContactRoleNameByEmailByProject(
	accountKey: string,
	emailURI: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	rolesToDelete: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/contacts/by-email-address/${emailURI}/roles?contactRoleNames=${rolesToDelete}`,
		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
			method: 'DELETE',
		}
	);

	return response;
}
