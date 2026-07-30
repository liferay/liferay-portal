/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {TRole} from '../../../helpers/HeadlessAdminUserApiHelper';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../utils/performLogin';

const test = mergeTests(apiHelpersTest, globalMenuPagesTest, loginTest());

let userAccount: TUserAccount;
let role: TRole;

test.beforeEach(async ({apiHelpers, page}) => {
	userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['ADD_ACCOUNT_ENTRY'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	await performLoginViaApi({
		page: apiHelpers.page,
		screenName: userAccount.alternateName,
	});
});

test.afterEach(async ({apiHelpers}) => {
	await performLoginViaApi({
		page: apiHelpers.page,
		screenName: 'test',
	});

	await apiHelpers.headlessAdminUser.deleteUserAccount(
		Number(userAccount.id)
	);

	await apiHelpers.headlessAdminUser.deleteRole(role.id);
});

test(
	'It shows categories according to user permissions',
	{tag: '@LPD-79704'},
	async ({globalMenuPage}) => {
		await globalMenuPage.openGlobalMenu();

		await expect(globalMenuPage.categoriesList).toBeVisible();

		const categories = globalMenuPage.categoriesList.getByRole('menuitem');

		await expect(categories).toHaveCount(1);

		const firstCategory = categories.first();

		await expect(firstCategory).toHaveAccessibleName('Control Panel');
		await expect(firstCategory).toHaveCSS('border-top-width', '0px');
	}
);

test(
	'The sites list is hidden if there is no site',
	{tag: '@LPD-79704'},
	async ({globalMenuPage}) => {
		await globalMenuPage.openGlobalMenu();

		await expect(globalMenuPage.sitesList).toBeHidden();
	}
);
