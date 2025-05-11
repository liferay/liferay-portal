/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';

export const test = mergeTests(
	accountsPagesTest,
	applicationsMenuPageTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-44889 Can view account roles with permissions', async ({
	accountRolesPage,
	accountsPage,
	apiHelpers,
	page,
}) => {
	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const accountAdminRole = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.assignUserToRole(
		accountAdminRole.externalReferenceCode,
		userAccount.id
	);

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account1',
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account2',
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		[userAccount.emailAddress]
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account2.id,
		[userAccount.emailAddress]
	);

	const accountRole1 = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['UPDATE', 'VIEW_ACCOUNT_ROLES'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountRole',
				scope: 3,
			},
		],
		roleType: 'account',
	});

	let rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account1.id
	);

	let role = rolesResponse?.items?.filter(
		(role) => role.name === accountRole1.name
	);

	await apiHelpers.headlessAdminUser.assignUserToAccountRole(
		account1.id,
		role[0].id,
		userAccount.id
	);

	const accountRole2 = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['UPDATE'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
		],
		roleType: 'account',
	});

	rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account2.id
	);

	role = rolesResponse?.items?.filter(
		(role) => role.name === accountRole2.name
	);

	await apiHelpers.headlessAdminUser.assignUserToAccountRole(
		account2.id,
		role[0].id,
		userAccount.id
	);

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	await accountsPage.gotoAccountAdmin();
	await (await accountsPage.accountsTable.cellLink(account1.name)).click();

	await expect(accountsPage.accountRolesTab).toBeVisible();

	await accountsPage.accountRolesTab.click();

	await expect(accountRolesPage.searchInput).toBeVisible();
	await expect(accountRolesPage.addNewRoleButton).not.toBeVisible();
	await expect(accountRolesPage.editRoleButton).not.toBeVisible();

	await accountsPage.gotoAccountAdmin();
	await (await accountsPage.accountsTable.cellLink(account2.name)).click();

	await expect(accountsPage.detailsTab).toBeVisible();
	await expect(accountsPage.accountRolesTab).not.toBeVisible();
});
