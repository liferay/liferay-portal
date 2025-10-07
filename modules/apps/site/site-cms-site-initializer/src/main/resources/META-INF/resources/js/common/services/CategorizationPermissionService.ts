/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IPermissionItem} from '../components/forms/PermissionsTable';
import ApiHelper from './ApiHelper';

async function putPermissions(
	permissionsAPIURL: string,
	permissions: IPermissionItem[]
) {
	return await ApiHelper.put<IPermissionItem[]>(
		permissionsAPIURL,
		permissions
	);
}

async function getPermissions(
	permissionsAPIURL: string
): Promise<IPermissionItem[]> {
	const {data, error} = await ApiHelper.get<{items: any}>(permissionsAPIURL);

	if (data) {
		return data.items;
	}

	throw new Error(
		error ||
			`GET request failed to fetch the entity's permissions from ${permissionsAPIURL}`
	);
}

export default {
	getPermissions,
	putPermissions,
};
