/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {TPermission, TRole} from '../../../helpers/HeadlessAdminUserApiHelper';
import getRandomString from '../../../utils/getRandomString';
import {userData} from '../../../utils/performLogin';

export async function addAccountRole(
	apiHelpers: DataApiHelpers,
	accountId: number = 0,
	rolePermissions: Array<TPermission>
) {
	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions,
		roleType: 'account',
	});

	const accountRole = await (
		await apiHelpers.headlessAdminUser.getAccountRoles(accountId)
	).items.find((item: TRole) => item.name === role.name);

	return {accountRole, role};
}

export async function initAccountAdministrator(apiHelpers: DataApiHelpers) {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		type: 'business',
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

	const userAccountAdmin =
		await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccountAdmin.alternateName] = {
		name: userAccountAdmin.givenName,
		password: 'test',
		surname: userAccountAdmin.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[userAccountAdmin.emailAddress]
	);

	const role = await (
		await apiHelpers.headlessAdminUser.getAccountRoles(account.id)
	).items.find((item: TRole) => item.name === 'Account Administrator');

	await apiHelpers.headlessAdminUser.assignUserToAccountRole(
		account.id,
		role.id,
		userAccountAdmin.id
	);

	return {account, userAccountAdmin};
}

export async function initAccountManager(apiHelpers: DataApiHelpers) {
	const organization = await apiHelpers.headlessAdminUser.postOrganization();

	const account = await apiHelpers.headlessAdminUser.postAccount();

	apiHelpers.data.push({id: account.id, type: 'account'});

	await apiHelpers.headlessAdminUser.assignAccountToOrganization(
		account.id,
		organization.id
	);

	const userAccountManager =
		await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccountManager.alternateName] = {
		name: userAccountManager.givenName,
		password: 'test',
		surname: userAccountManager.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[userAccountManager.emailAddress]
	);

	await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
		organization.id,
		userAccountManager.emailAddress
	);

	const role = await apiHelpers.headlessAdminUser.getRoleByName(
		'Account Manager',
		'rolePermissions'
	);

	await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
		String(role.id),
		userAccountManager.id,
		organization.id
	);

	return {account, organization, role, userAccountManager};
}
