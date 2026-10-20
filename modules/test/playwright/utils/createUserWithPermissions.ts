/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../helpers/ApiHelpers';
import {getRandomInt} from './getRandomInt';
import {userData} from './performLogin';

export type ActionId =
	| 'ACCESS_IN_CONTROL_PANEL'
	| 'ADD_ARTICLE'
	| 'ADD_LAYOUT'
	| 'ADD_OBJECT_ENTRY'
	| 'EDIT_ORGANIZATIONS'
	| 'MANAGE_ADDRESSES'
	| 'MANAGE_AVAILABLE_ACCOUNTS'
	| 'MANAGE_COMMERCE_CURRENCIES'
	| 'MANAGE_USERS'
	| 'UPDATE'
	| 'UPDATE_LAYOUT_ADVANCED_OPTIONS'
	| 'UPDATE_LAYOUT_BASIC'
	| 'UPDATE_LAYOUT_LIMITED'
	| 'VIEW'
	| 'VIEW_ADDRESSES'
	| 'VIEW_COMMERCE_CHANNELS'
	| 'VIEW_COMMERCE_DISCOUNTS'
	| 'VIEW_COMMERCE_ORDERS'
	| 'VIEW_COMMERCE_TERM_ENTRY'
	| 'VIEW_CONTROL_PANEL'
	| 'VIEW_OPEN_COMMERCE_ORDERS'
	| 'VIEW_ORGANIZATIONS'
	| 'VIEW_SITE_ADMINISTRATION';

export type RolePermission = {
	actionIds: ActionId[];
	primaryKey: string;
	resourceName: string;
	scope: number;
};

export default async function createUserWithPermissions({
	apiHelpers,
	name = 'role' + getRandomInt(),
	rolePermissions,
}: {
	apiHelpers: ApiHelpers;
	name?: string;
	rolePermissions: RolePermission[];
}) {
	const role = await apiHelpers.headlessAdminUser.postRole({
		name,
		rolePermissions,
	});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	return user;
}
